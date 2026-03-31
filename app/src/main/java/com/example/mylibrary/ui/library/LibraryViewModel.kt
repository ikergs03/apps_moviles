package com.example.mylibrary.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _searchQuery = MutableLiveData("")
    private val _filterType = MutableLiveData<ItemType?>(null)

    val items: LiveData<List<LibraryItem>> = _searchQuery.switchMap { query ->
        _filterType.switchMap { type ->
            when {
                query.isNotBlank() -> repository.searchItems(query).asLiveData()
                type != null -> repository.getItemsByType(type.name).asLiveData()
                else -> repository.getAllItems().asLiveData()
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: ItemType?) {
        _filterType.value = type
    }

    fun deleteItem(item: LibraryItem) {
        viewModelScope.launch { repository.deleteItem(item) }
    }
}
