package com.example.mylibrary.data.repository

import com.example.mylibrary.data.local.LibraryItemDao
import com.example.mylibrary.data.local.LibraryItemEntity
import com.example.mylibrary.data.model.LibraryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val dao: LibraryItemDao
) {
    fun getAllItems(): Flow<List<LibraryItem>> =
        dao.getAllItems().map { entities -> entities.map { it.toLibraryItem() } }

    fun getItemsByType(type: String): Flow<List<LibraryItem>> =
        dao.getItemsByType(type).map { entities -> entities.map { it.toLibraryItem() } }

    fun searchItems(query: String): Flow<List<LibraryItem>> =
        dao.searchItems("%$query%").map { entities -> entities.map { it.toLibraryItem() } }

    fun getItemsByStatus(status: String): Flow<List<LibraryItem>> =
        dao.getItemsByStatus(status).map { entities -> entities.map { it.toLibraryItem() } }

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
