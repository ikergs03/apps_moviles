package com.example.mylibrary.data.model

data class LibraryItem(
    val id: Long = 0,
    val type: ItemType,
    val title: String,
    val author: String = "",
    val year: String = "",
    val genre: String = "",
    val synopsis: String = "",
    val coverUrl: String = "",
    val status: ItemStatus = ItemStatus.PENDING,
    val rating: Float = 0f,
    val review: String = "",
    val tags: String = ""
)
