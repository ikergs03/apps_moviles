package com.example.mylibrary.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment() {
    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditViewModel by viewModels()
    private val args: AddEditFragmentArgs by navArgs()
    private var currentSuggestions: List<LibraryItem> = emptyList()
    private var isPopulatingForm = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()

        if (args.itemId > 0) {
            viewModel.loadItem(args.itemId)
        }

        viewModel.item.observe(viewLifecycleOwner) { item ->
            item?.let { populateForm(it) }
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            currentSuggestions = suggestions
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                suggestions.map { it.title }
            )
            binding.etTitle.setAdapter(adapter)

            if (suggestions.isNotEmpty()) {
                binding.etTitle.showDropDown()
            } else {
                binding.etTitle.dismissDropDown()
            }
        }

        binding.etTitle.setOnItemClickListener { _, _, position, _ ->
            if (position in currentSuggestions.indices) {
                viewModel.selectSuggestion(currentSuggestions[position])
            }
        }

        binding.etTitle.doAfterTextChanged { editable ->
            if (isPopulatingForm) return@doAfterTextChanged

            val query = editable?.toString()?.trim().orEmpty()
            viewModel.searchSuggestions(query, selectedType())
        }

        binding.etTitle.setOnEditorActionListener { _, _, _ ->
            val query = binding.etTitle.text.toString().trim()
            val type = selectedType()
            if (query.length >= 3) viewModel.searchSuggestions(query, type)
            false
        }

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isPopulatingForm) return

                val query = binding.etTitle.text.toString().trim()
                viewModel.searchSuggestions(query, selectedType())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        viewModel.saved.observe(viewLifecycleOwner) { saved ->
            if (saved) findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun setupSpinners() {
        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Libro", "Película", "Videojuego")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter

        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Pendiente", "En progreso", "Completado", "Abandonado")
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = statusAdapter
    }

    private fun selectedType(): ItemType = when (binding.spinnerType.selectedItemPosition) {
        1 -> ItemType.MOVIE
        2 -> ItemType.VIDEOGAME
        else -> ItemType.BOOK
    }

    private fun selectedStatus(): ItemStatus = when (binding.spinnerStatus.selectedItemPosition) {
        1 -> ItemStatus.IN_PROGRESS
        2 -> ItemStatus.COMPLETED
        3 -> ItemStatus.ABANDONED
        else -> ItemStatus.PENDING
    }

    private fun populateForm(item: LibraryItem) {
        isPopulatingForm = true
        binding.etTitle.setText(item.title)
        binding.etAuthor.setText(item.author)
        binding.etYear.setText(item.year)
        binding.etGenre.setText(item.genre)
        binding.etSynopsis.setText(item.synopsis)
        binding.etCoverUrl.setText(item.coverUrl)
        binding.etTags.setText(item.tags)
        binding.ratingBar.rating = item.rating
        binding.etReview.setText(item.review)
        binding.spinnerType.setSelection(when (item.type) {
            ItemType.BOOK -> 0
            ItemType.MOVIE -> 1
            ItemType.VIDEOGAME -> 2
        })
        binding.spinnerStatus.setSelection(when (item.status) {
            ItemStatus.PENDING -> 0
            ItemStatus.IN_PROGRESS -> 1
            ItemStatus.COMPLETED -> 2
            ItemStatus.ABANDONED -> 3
        })
        isPopulatingForm = false
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            binding.etTitle.error = "El título es obligatorio"
            return
        }
        val item = LibraryItem(
            id = args.itemId,
            type = selectedType(),
            title = title,
            author = binding.etAuthor.text.toString().trim(),
            year = binding.etYear.text.toString().trim(),
            genre = binding.etGenre.text.toString().trim(),
            synopsis = binding.etSynopsis.text.toString().trim(),
            coverUrl = binding.etCoverUrl.text.toString().trim(),
            tags = binding.etTags.text.toString().trim(),
            status = selectedStatus(),
            rating = binding.ratingBar.rating,
            review = binding.etReview.text.toString().trim()
        )
        viewModel.saveItem(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
