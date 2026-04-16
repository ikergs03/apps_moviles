package com.example.mylibrary.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.mylibrary.R
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.databinding.FragmentLibraryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LibraryAdapter(
            onItemClick = { item ->
                val action = LibraryFragmentDirections.actionLibraryFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            },
            onItemLongClick = { item ->
                showDeleteDialog(item)
            }
        )
        binding.rvLibrary.adapter = adapter
        binding.rvLibrary.setHasFixedSize(true)

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
            if (items.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_addEditFragment)
        }

        setupFilterChips()
        setupMenu()
    }

    private fun setupFilterChips() {
        val chips = listOf(
            binding.chipAll to null,
            binding.chipBooks to ItemType.BOOK,
            binding.chipMovies to ItemType.MOVIE,
            binding.chipGames to ItemType.VIDEOGAME
        )
        chips.forEach { (chip, type) ->
            chip.setOnClickListener {
                viewModel.setFilterType(type)
                chips.forEach { (c, _) -> c.isChecked = false }
                chip.isChecked = true
            }
        }
        binding.chipAll.isChecked = true
    }

    private fun showDeleteDialog(item: LibraryItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar")
            .setMessage("¿Eliminar \"${item.title}\"?")
            .setPositiveButton("Eliminar") { _, _ -> viewModel.deleteItem(item) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.menu_library, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setSearchQuery(newText ?: "")
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(item: MenuItem) = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
