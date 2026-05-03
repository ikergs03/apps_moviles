package com.example.mylibrary.data.local

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [LibraryItemEntity::class], version = 3, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun libraryItemDao(): LibraryItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE library_items ADD COLUMN ownerId TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE library_items ADD COLUMN syncId TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
