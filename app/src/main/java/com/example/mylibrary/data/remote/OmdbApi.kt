package com.example.mylibrary.data.remote

import com.example.mylibrary.data.remote.dto.OmdbDetailResponse
import com.example.mylibrary.data.remote.dto.OmdbSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET(".")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String
    ): OmdbSearchResponse

    @GET(".")
    suspend fun getMovieById(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String
    ): OmdbDetailResponse
}
