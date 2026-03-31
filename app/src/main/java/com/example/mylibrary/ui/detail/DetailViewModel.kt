package com.example.mylibrary.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.data.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _item = MutableLiveData<LibraryItem?>()
    val item: LiveData<LibraryItem?> = _item

    fun loadItem(id: Long) {
        viewModelScope.launch {
            _item.value = repository.getItemById(id)
        }
    }

    fun updateStatus(status: ItemStatus) {
        val current = _item.value ?: return
        val updated = current.copy(status = status)
        viewModelScope.launch {
            repository.updateItem(updated)
            _item.value = updated
        }
    }

    fun deleteItem(onDeleted: () -> Unit) {
        val current = _item.value ?: return
        viewModelScope.launch {
            repository.deleteItem(current)
            onDeleted()
        }
    }
}
