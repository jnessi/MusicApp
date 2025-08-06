package com.example.musicapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(
    private var trackList: MutableList<TrackItemResponse> = mutableListOf(),
    private val onFavoriteToggle: (trackId: Long, position: Int) -> Unit, // Lambda to handle adding/removing a song from favorites
    private val isFavorite: (trackId: Long) -> Boolean, // Lambda that determines if a song is currently marked as favorite
    private val onPlayClicked: (track: TrackItemResponse) -> Unit // Lambda that handles click events to launch the PlayerActivity
) :
    RecyclerView.Adapter<TrackViewHolder>() {


    fun updateList(list: List<TrackItemResponse>) {
        trackList.clear()
        trackList.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        val item = trackList[position]
        val isFav = isFavorite(item.id)
        holder.bind(item, position, isFav, onFavoriteToggle, onPlayClicked)
    }

    override fun getItemCount() = trackList.size


}