package com.example.musicapp

import androidx.compose.ui.text.LinkAnnotation
import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("data") val data : List<TrackItemResponse>
)

data class TrackItemResponse(
    @SerializedName("id") val id : Long,
    @SerializedName("title") val title : String,
    @SerializedName("preview") val preview : String,
    @SerializedName("artist") val artist : TrackArtistDetails,
    @SerializedName("album") val album : TrackAlbumDetails,
    @SerializedName("rank")val rank: Int,
    @SerializedName("duration") val duration: Int
)

data class TrackAlbumDetails(
    @SerializedName("title") val name: String,
    @SerializedName("cover_medium") val cover: String
)

data class TrackArtistDetails(
    @SerializedName("name") val name: String,
    @SerializedName("picture_medium") val picture: String

)
