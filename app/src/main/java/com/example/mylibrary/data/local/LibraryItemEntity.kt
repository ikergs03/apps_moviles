package com.example.mylibrary.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mylibrary.data.model.ItemStatus
import com.example.mylibrary.data.model.ItemType
import com.example.mylibrary.data.model.LibraryItem

@Entity(tableName = "library_items")
data class LibraryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val title: String,
    val author: String = "",
    val year: String = "",
    val genre: String = "",
    val synopsis: String = "",
    val coverUrl: String = "",
    val status: String = ItemStatus.PENDING.name,
    val rating: Float = 0f,
    val review: String = "",
    val tags: String = ""
) {
    fun toLibraryItem() = LibraryItem(
        id = id,
        type = ItemType.valueOf(type),
        title = title,
        author = author,
        year = year,
        genre = genre,
        synopsis = synopsis,
        coverUrl = coverUrl,
        status = ItemStatus.valueOf(status),
        rating = rating,
        review = review,
        tags = tags
    )

    companion object {
        fun fromLibraryItem(item: LibraryItem) = LibraryItemEntity(
            id = item.id,
            type = item.type.name,
            title = item.title,
            author = item.author,
            year = item.year,
            genre = item.genre,
            synopsis = item.synopsis,
            coverUrl = item.coverUrl,
            status = item.status.name,
            rating = item.rating,
            review = item.review,
            tags = item.tags
        )
    }
}
