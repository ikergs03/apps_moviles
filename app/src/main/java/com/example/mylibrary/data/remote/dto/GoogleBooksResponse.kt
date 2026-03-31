package com.example.mylibrary.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<GoogleBookItem>? = null
)

data class GoogleBookItem(
    @SerializedName("id") val id: String = "",
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo = VolumeInfo()
)

data class VolumeInfo(
    @SerializedName("title") val title: String = "",
    @SerializedName("authors") val authors: List<String>? = null,
    @SerializedName("publishedDate") val publishedDate: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName("imageLinks") val imageLinks: ImageLinks? = null
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String = "",
    @SerializedName("smallThumbnail") val smallThumbnail: String = ""
)
