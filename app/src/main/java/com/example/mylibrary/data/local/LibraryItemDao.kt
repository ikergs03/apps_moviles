package com.example.mylibrary.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LibraryItemDao {
    @Query("SELECT * FROM library_items ORDER BY title ASC")
    fun getAllItems(): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE ownerId = :userId ORDER BY title ASC")
    fun getAllItemsForUser(userId: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE id = :id")
    suspend fun getItemById(id: Long): LibraryItemEntity?

    @Query("SELECT * FROM library_items WHERE type = :type ORDER BY title ASC")
    fun getItemsByType(type: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE ownerId = :userId AND type = :type ORDER BY title ASC")
    fun getItemsByTypeForUser(type: String, userId: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE title LIKE :query OR author LIKE :query ORDER BY title ASC")
    fun searchItems(query: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE ownerId = :userId AND (title LIKE :query OR author LIKE :query) ORDER BY title ASC")
    fun searchItemsForUser(query: String, userId: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE status = :status ORDER BY title ASC")
    fun getItemsByStatus(status: String): LiveData<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE ownerId = :userId AND status = :status ORDER BY title ASC")
    fun getItemsByStatusForUser(status: String, userId: String): LiveData<List<LibraryItemEntity>>

    @Query("UPDATE library_items SET ownerId = :ownerId WHERE ownerId IS NULL")
    suspend fun assignOwnerToUnownedItems(ownerId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LibraryItemEntity): Long

    @Update
    suspend fun updateItem(item: LibraryItemEntity)

    @Delete
    suspend fun deleteItem(item: LibraryItemEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}
