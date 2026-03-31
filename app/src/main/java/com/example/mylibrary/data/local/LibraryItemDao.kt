package com.example.mylibrary.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryItemDao {
    @Query("SELECT * FROM library_items ORDER BY title ASC")
    fun getAllItems(): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE id = :id")
    suspend fun getItemById(id: Long): LibraryItemEntity?

    @Query("SELECT * FROM library_items WHERE type = :type ORDER BY title ASC")
    fun getItemsByType(type: String): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE title LIKE :query OR author LIKE :query ORDER BY title ASC")
    fun searchItems(query: String): Flow<List<LibraryItemEntity>>

    @Query("SELECT * FROM library_items WHERE status = :status ORDER BY title ASC")
    fun getItemsByStatus(status: String): Flow<List<LibraryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LibraryItemEntity): Long

    @Update
    suspend fun updateItem(item: LibraryItemEntity)

    @Delete
    suspend fun deleteItem(item: LibraryItemEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)
}
