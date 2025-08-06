package com.example.musicapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Fetches a list of tracks by searching the artist or song name
    @GET("search")
    suspend fun getTrackByArtist(@Query("q") name: String): Response<TrackResponse>

    // Retrieves the top tracks from a specific genre chart (or global chart if ID = 0)
    @GET("chart/{id}/tracks")
    suspend fun getChart(@Path("id") id: Int = 0): Response<TrackResponse>

    // Gets detailed information about a specific track by its ID
    @GET("track/{id}")
    suspend fun getTrackById(@Path("id") trackId: Long): Response<TrackItemResponse>

}