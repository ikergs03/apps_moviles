package com.example.mylibrary.di

import android.content.Context
import androidx.room.Room
import com.example.mylibrary.data.local.LibraryDatabase
import com.example.mylibrary.data.local.LibraryItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LibraryDatabase =
        Room.databaseBuilder(context, LibraryDatabase::class.java, "library_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLibraryItemDao(database: LibraryDatabase): LibraryItemDao =
        database.libraryItemDao()
}
