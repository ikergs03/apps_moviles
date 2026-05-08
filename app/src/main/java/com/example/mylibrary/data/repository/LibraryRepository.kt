package com.example.mylibrary.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.mylibrary.data.local.LibraryItemDao
import com.example.mylibrary.data.local.LibraryItemEntity
import com.example.mylibrary.data.model.LibraryItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Singleton
class LibraryRepository @Inject constructor(
    private val dao: LibraryItemDao,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val ADMIN_EMAIL = "admin@example.com"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_LIBRARY_ITEMS = "library_items"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null
    private var firestoreSyncJob: Job? = null

    private fun currentUid(): String? = auth.currentUser?.uid

    private fun userCollection(userId: String) =
        firestore.collection(COLLECTION_USERS).document(userId).collection(COLLECTION_LIBRARY_ITEMS)

    private fun LibraryItemEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
        "syncId" to syncId,
        "ownerId" to ownerId,
        "type" to type,
        "title" to title,
        "author" to author,
        "year" to year,
        "genre" to genre,
        "synopsis" to synopsis,
        "coverUrl" to coverUrl,
        "status" to status,
        "rating" to rating,
        "review" to review,
        "tags" to tags
    )

    private fun DocumentSnapshot.toLibraryItemEntity(ownerId: String): LibraryItemEntity? {
        return try {
            LibraryItemEntity(
                type = getString("type") ?: return null,
                title = getString("title") ?: return null,
                author = getString("author") ?: "",
                year = getString("year") ?: "",
                genre = getString("genre") ?: "",
                synopsis = getString("synopsis") ?: "",
                coverUrl = getString("coverUrl") ?: "",
                status = getString("status") ?: "PENDING",
                rating = (getDouble("rating") ?: 0.0).toFloat(),
                review = getString("review") ?: "",
                tags = getString("tags") ?: "",
                ownerId = getString("ownerId") ?: ownerId,
                syncId = id
            )
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun ensureSyncId(entity: LibraryItemEntity): LibraryItemEntity {
        if (entity.syncId.isNotBlank()) return entity
        val updated = entity.copy(syncId = UUID.randomUUID().toString())
        dao.updateItem(updated)
        return updated
    }

    private suspend fun upsertLocalEntity(entity: LibraryItemEntity) {
        val current = dao.getItemBySyncId(entity.ownerId ?: return, entity.syncId)
        if (current == null) {
            dao.insertItem(entity)
        } else if (current.id != entity.id || current != entity) {
            dao.updateItem(entity.copy(id = current.id))
        }
    }

    private suspend fun pushLocalItemsToFirestore(userId: String) {
        val items = dao.getAllItemsForUserOnce(userId)
        items.forEach { entity ->
            val synced = ensureSyncId(entity.copy(ownerId = userId))
            runCatching {
                userCollection(userId).document(synced.syncId).set(synced.toFirestoreMap()).await()
            }
        }
    }

    private suspend fun backfillMissingSyncIds(userId: String) {
        val items = dao.getAllItemsForUserOnce(userId)
        items.filter { it.syncId.isBlank() }.forEach { entity ->
            val synced = entity.copy(syncId = UUID.randomUUID().toString(), ownerId = userId)
            dao.updateItem(synced)
        }
    }

    fun startFirestoreSync(scope: CoroutineScope) {
        val user = auth.currentUser ?: return
        stopFirestoreSync()

        firestoreSyncJob = scope.launch(Dispatchers.IO) {
            backfillMissingSyncIds(user.uid)
            pushLocalItemsToFirestore(user.uid)
        }

        firestoreListener = userCollection(user.uid).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            scope.launch(Dispatchers.IO) {
                snapshot.documentChanges.forEach { change ->
                    when (change.type) {
                        DocumentChange.Type.ADDED,
                        DocumentChange.Type.MODIFIED -> {
                            change.document.toLibraryItemEntity(user.uid)?.let { remoteEntity ->
                                upsertLocalEntity(remoteEntity)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            dao.deleteItemBySyncId(user.uid, change.document.id)
                        }
                    }
                }
            }
        }
    }

    fun stopFirestoreSync() {
        firestoreListener?.remove()
        firestoreListener = null
        firestoreSyncJob?.cancel()
        firestoreSyncJob = null
    }

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
        val entity = ensureSyncId(
            LibraryItemEntity.fromLibraryItem(item).copy(ownerId = uid)
        )
        val insertedId = dao.insertItem(entity)

        if (uid != null) {
            runCatching {
                userCollection(uid).document(entity.syncId).set(entity.toFirestoreMap()).await()
            }
        }

        return insertedId
    }

    suspend fun updateItem(item: LibraryItem) {
        val uid = currentUid()
        val existing = dao.getItemById(item.id)
        val entity = LibraryItemEntity.fromLibraryItem(item).copy(
            id = existing?.id ?: item.id,
            ownerId = existing?.ownerId ?: uid,
            syncId = existing?.syncId.orEmpty().ifBlank { UUID.randomUUID().toString() }
        )
        dao.updateItem(entity)

        val owner = entity.ownerId ?: uid
        if (owner != null) {
            runCatching {
                userCollection(owner).document(entity.syncId).set(entity.toFirestoreMap()).await()
            }
        }
    }

    suspend fun deleteItem(item: LibraryItem) {
        val existing = dao.getItemById(item.id)
        if (existing != null) {
            dao.deleteItem(existing)
            val owner = existing.ownerId ?: currentUid()
            if (owner != null && existing.syncId.isNotBlank()) {
                runCatching {
                    userCollection(owner).document(existing.syncId).delete().await()
                }
            }
            return
        }

        dao.deleteItem(LibraryItemEntity.fromLibraryItem(item))
    }

    suspend fun deleteItemById(id: Long) =
        dao.deleteItemById(id)

    suspend fun claimLegacyItemsForAdmin() {
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        if (!email.equals(ADMIN_EMAIL, ignoreCase = true)) return
        dao.assignOwnerToUnownedItems(user.uid)
    }
}
