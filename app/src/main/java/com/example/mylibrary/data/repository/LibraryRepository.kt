package com.example.mylibrary.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.example.mylibrary.data.local.LibraryItemDao
import com.example.mylibrary.data.local.LibraryItemEntity
import com.example.mylibrary.data.model.LibraryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val dao: LibraryItemDao,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val ADMIN_EMAIL = "admin@example.com"
    }

    private fun currentUid(): String? = auth.currentUser?.uid

    fun getAllItems(): LiveData<List<LibraryItem>> =
        dao.getAllItemsForUser(currentUid() ?: "").map { entities: List<LibraryItemEntity> ->
            entities.map { entity -> entity.toLibraryItem() }
        }

    fun getItemsByType(type: String): LiveData<List<LibraryItem>> =
        dao.getItemsByTypeForUser(type, currentUid() ?: "").map { entities: List<LibraryItemEntity> ->
            entities.map { entity -> entity.toLibraryItem() }
        }

    fun searchItems(query: String): LiveData<List<LibraryItem>> =
        dao.searchItemsForUser("%$query%", currentUid() ?: "").map { entities: List<LibraryItemEntity> ->
            entities.map { entity -> entity.toLibraryItem() }
        }

    fun getItemsByStatus(status: String): LiveData<List<LibraryItem>> =
        dao.getItemsByStatusForUser(status, currentUid() ?: "").map { entities: List<LibraryItemEntity> ->
            entities.map { entity -> entity.toLibraryItem() }
        }

    suspend fun getItemById(id: Long): LibraryItem? =
        dao.getItemById(id)?.toLibraryItem()

    suspend fun insertItem(item: LibraryItem): Long {
        val uid = currentUid()
        val entity = LibraryItemEntity.fromLibraryItem(item).copy(ownerId = uid)
        return dao.insertItem(entity)
    }

    suspend fun updateItem(item: LibraryItem) {
        val uid = currentUid()
        dao.updateItem(LibraryItemEntity.fromLibraryItem(item).copy(ownerId = uid))
    }

    suspend fun deleteItem(item: LibraryItem) =
        dao.deleteItem(LibraryItemEntity.fromLibraryItem(item))

    suspend fun deleteItemById(id: Long) =
        dao.deleteItemById(id)

    suspend fun claimLegacyItemsForAdmin() {
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        if (!email.equals(ADMIN_EMAIL, ignoreCase = true)) return
        dao.assignOwnerToUnownedItems(user.uid)
    }
}
