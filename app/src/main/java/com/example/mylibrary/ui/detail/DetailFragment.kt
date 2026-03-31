package com.example.mylibrary.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.mylibrary.R
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.databinding.FragmentDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadItem(args.itemId)

        viewModel.item.observe(viewLifecycleOwner) { item ->
            item?.let { displayItem(it) }
        }

        setupMenu()
    }

    private fun displayItem(item: LibraryItem) {
        binding.tvTitle.text = item.title
        binding.tvAuthor.text = item.author.ifBlank { "—" }
        binding.tvYear.text = item.year.ifBlank { "—" }
        binding.tvGenre.text = item.genre.ifBlank { "—" }
        binding.tvSynopsis.text = item.synopsis.ifBlank { "Sin sinopsis disponible." }
        binding.tvType.text = when (item.type) {
            ItemType.BOOK -> "📚 Libro"
            ItemType.MOVIE -> "🎬 Película"
            ItemType.VIDEOGAME -> "🎮 Videojuego"
        }
        binding.ratingBar.rating = item.rating
        binding.tvReview.text = item.review.ifBlank { "Sin reseña." }

        binding.chipGroupTags.removeAllViews()
        if (item.tags.isNotBlank()) {
            item.tags.split(",").forEach { tag ->
                val chip = Chip(requireContext())
                chip.text = tag.trim()
                binding.chipGroupTags.addView(chip)
            }
        }

        val statusMap = mapOf(
            binding.chipPending to ItemStatus.PENDING,
            binding.chipInProgress to ItemStatus.IN_PROGRESS,
            binding.chipCompleted to ItemStatus.COMPLETED,
            binding.chipAbandoned to ItemStatus.ABANDONED
        )
        statusMap.forEach { (chip, status) ->
            chip.isChecked = item.status == status
            chip.setOnClickListener { viewModel.updateStatus(status) }
        }

        if (item.coverUrl.isNotBlank()) {
            Glide.with(this)
                .load(item.coverUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.ivCover)
        } else {
            binding.ivCover.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.menu_detail, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_edit -> {
                        val action = DetailFragmentDirections
                            .actionDetailFragmentToAddEditFragment(args.itemId)
                        findNavController().navigate(action)
                        true
                    }
                    R.id.action_delete -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Eliminar")
                            .setMessage("¿Eliminar este elemento?")
                            .setPositiveButton("Eliminar") { _, _ ->
                                viewModel.deleteItem { findNavController().navigateUp() }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
