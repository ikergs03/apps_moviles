package com.example.mylibrary.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.mylibrary.data.local.LibraryItemDao
import com.example.mylibrary.data.local.LibraryItemEntity
import com.example.mylibrary.data.model.LibraryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val dao: LibraryItemDao
) {
    fun getAllItems(): LiveData<List<LibraryItem>> =
        Transformations.map(dao.getAllItems()) { entities -> entities.map { it.toLibraryItem() } }

    fun getItemsByType(type: String): LiveData<List<LibraryItem>> =
        Transformations.map(dao.getItemsByType(type)) { entities -> entities.map { it.toLibraryItem() } }

    fun searchItems(query: String): LiveData<List<LibraryItem>> =
        Transformations.map(dao.searchItems("%$query%")) { entities -> entities.map { it.toLibraryItem() } }

    fun getItemsByStatus(status: String): LiveData<List<LibraryItem>> =
        Transformations.map(dao.getItemsByStatus(status)) { entities -> entities.map { it.toLibraryItem() } }

    suspend fun getItemById(id: Long): LibraryItem? =
        dao.getItemById(id)?.toLibraryItem()

    suspend fun insertItem(item: LibraryItem): Long =
        dao.insertItem(LibraryItemEntity.fromLibraryItem(item))

    suspend fun updateItem(item: LibraryItem) =
        dao.updateItem(LibraryItemEntity.fromLibraryItem(item))

    suspend fun deleteItem(item: LibraryItem) =
        dao.deleteItem(LibraryItemEntity.fromLibraryItem(item))

    suspend fun deleteItemById(id: Long) =
        dao.deleteItemById(id)
}
