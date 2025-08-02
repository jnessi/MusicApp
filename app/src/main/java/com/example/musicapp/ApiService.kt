package com.example.musicapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    suspend fun getCancionesDeArtista(@Query("q") name: String): Response<TrackResponse>

    @GET("chart/{id}/tracks")
    suspend fun getChart(@Path("id") id: Int = 0): Response<TrackResponse>

}