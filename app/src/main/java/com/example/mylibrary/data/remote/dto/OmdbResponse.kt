package com.example.mylibrary.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OmdbSearchResponse(
    @SerializedName("Search") val search: List<OmdbSearchItem>? = null,
    @SerializedName("totalResults") val totalResults: String = "0",
    @SerializedName("Response") val response: String = "False"
)

data class OmdbSearchItem(
    @SerializedName("Title") val title: String = "",
    @SerializedName("Year") val year: String = "",
    @SerializedName("imdbID") val imdbId: String = "",
    @SerializedName("Type") val type: String = "",
    @SerializedName("Poster") val poster: String = ""
)

data class OmdbDetailResponse(
    @SerializedName("Title") val title: String = "",
    @SerializedName("Year") val year: String = "",
    @SerializedName("Genre") val genre: String = "",
    @SerializedName("Director") val director: String = "",
    @SerializedName("Plot") val plot: String = "",
    @SerializedName("Poster") val poster: String = "",
    @SerializedName("imdbID") val imdbId: String = "",
    @SerializedName("Response") val response: String = "False"
)
