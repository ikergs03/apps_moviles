package com.example.mylibrary.ui.addedit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.data.repository.LibraryRepository
import com.example.mylibrary.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _item = MutableLiveData<LibraryItem?>()
    val item: LiveData<LibraryItem?> = _item

    private val _suggestions = MutableLiveData<List<LibraryItem>>(emptyList())
    val suggestions: LiveData<List<LibraryItem>> = _suggestions

    private val _saved = MutableLiveData(false)
    val saved: LiveData<Boolean> = _saved

    fun loadItem(id: Long) {
        viewModelScope.launch {
            _item.value = libraryRepository.getItemById(id)
        }
    }

    fun saveItem(item: LibraryItem) {
        viewModelScope.launch {
            if (item.id == 0L) libraryRepository.insertItem(item)
            else libraryRepository.updateItem(item)
            _saved.value = true
        }
    }

    fun searchSuggestions(query: String, type: ItemType) {
        if (query.length < 3) {
            _suggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            _suggestions.value = when (type) {
                ItemType.BOOK -> searchRepository.searchBooks(query)
                ItemType.MOVIE -> searchRepository.searchMovies(query)
                ItemType.VIDEOGAME -> emptyList()
            }
        }
    }

    fun selectSuggestion(item: LibraryItem) {
        _item.value = item
        _suggestions.value = emptyList()
    }
}
