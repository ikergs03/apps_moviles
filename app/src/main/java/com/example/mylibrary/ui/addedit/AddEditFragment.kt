package com.example.mylibrary.ui.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mylibrary.R
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

        setupToggleGroups()

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
            binding.tilTitle.error = null
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

        binding.toggleType.addOnButtonCheckedListener { _, _, isChecked ->
            if (!isChecked || isPopulatingForm) return@addOnButtonCheckedListener
            val query = binding.etTitle.text.toString().trim()
            viewModel.searchSuggestions(query, selectedType())
        }

        viewModel.saved.observe(viewLifecycleOwner) { saved ->
            if (saved) findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun setupToggleGroups() {
        binding.toggleType.check(binding.btnTypeBook.id)
        binding.toggleStatus.check(binding.btnStatusPending.id)
    }

    private fun selectedType(): ItemType = when (binding.toggleType.checkedButtonId) {
        binding.btnTypeMovie.id -> ItemType.MOVIE
        binding.btnTypeVideogame.id -> ItemType.VIDEOGAME
        else -> ItemType.BOOK
    }

    private fun selectedStatus(): ItemStatus = when (binding.toggleStatus.checkedButtonId) {
        binding.btnStatusInProgress.id -> ItemStatus.IN_PROGRESS
        binding.btnStatusCompleted.id -> ItemStatus.COMPLETED
        binding.btnStatusAbandoned.id -> ItemStatus.ABANDONED
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
        binding.toggleType.check(when (item.type) {
            ItemType.BOOK -> binding.btnTypeBook.id
            ItemType.MOVIE -> binding.btnTypeMovie.id
            ItemType.VIDEOGAME -> binding.btnTypeVideogame.id
        })
        binding.toggleStatus.check(when (item.status) {
            ItemStatus.PENDING -> binding.btnStatusPending.id
            ItemStatus.IN_PROGRESS -> binding.btnStatusInProgress.id
            ItemStatus.COMPLETED -> binding.btnStatusCompleted.id
            ItemStatus.ABANDONED -> binding.btnStatusAbandoned.id
        })
        isPopulatingForm = false
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            binding.tilTitle.error = getString(R.string.error_titulo_obligatorio)
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
