package com.example.mylibrary.data.repository

import com.example.mylibrary.BuildConfig
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem
import com.example.mylibrary.data.remote.GoogleBooksApi
import com.example.mylibrary.data.remote.OmdbApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val googleBooksApi: GoogleBooksApi,
    private val omdbApi: OmdbApi
) {
    suspend fun searchBooks(query: String): List<LibraryItem> {
        return try {
            val response = googleBooksApi.searchBooks(query)
            response.items?.map { item ->
                val info = item.volumeInfo
                val thumbnail = info.imageLinks?.thumbnail
                    ?.replace("http://", "https://") ?: ""
                LibraryItem(
                    type = ItemType.BOOK,
                    title = info.title,
                    author = info.authors?.joinToString(", ") ?: "",
                    year = info.publishedDate.take(4),
                    genre = info.categories?.firstOrNull() ?: "",
                    synopsis = info.description,
                    coverUrl = thumbnail,
                    status = ItemStatus.PENDING
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchMovies(query: String): List<LibraryItem> {
        return try {
            val response = omdbApi.searchMovies(query, BuildConfig.OMDB_API_KEY)
            if (response.response == "True") {
                response.search?.map { item ->
                    LibraryItem(
                        type = ItemType.MOVIE,
                        title = item.title,
                        year = item.year,
                        coverUrl = if (item.poster != "N/A") item.poster else "",
                        status = ItemStatus.PENDING
                    )
                } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
