package com.example.musicapp

import androidx.compose.ui.text.LinkAnnotation
import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("data") val data: List<TrackItemResponse>
)

// Represents a single track item with all relevant metadata.
data class TrackItemResponse(
    @SerializedName("id") val id: Long, // Unique ID of the track
    @SerializedName("title") val title: String, // Unique ID of the track
    @SerializedName("preview") val preview: String, // Unique ID of the track
    @SerializedName("artist") val artist: TrackArtistDetails, // Artist details
    @SerializedName("album") val album: TrackAlbumDetails, // Artist details
    @SerializedName("rank") val rank: Int, // Artist details
    @SerializedName("duration") val duration: Int // Artist details
)

// Holds album-specific metadata for a track.
data class TrackAlbumDetails(
    @SerializedName("title") val name: String, // Album title
    @SerializedName("cover_medium") val cover: String // Album cover image URL
)

// Holds artist-specific metadata for a track.
data class TrackArtistDetails(
    @SerializedName("name") val name: String, // Artist name
    @SerializedName("picture_medium") val picture: String // Artist image URL

)
