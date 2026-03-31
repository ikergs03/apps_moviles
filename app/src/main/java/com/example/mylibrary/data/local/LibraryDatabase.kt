package com.example.mylibrary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LibraryItemEntity::class], version = 1, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun libraryItemDao(): LibraryItemDao
}
