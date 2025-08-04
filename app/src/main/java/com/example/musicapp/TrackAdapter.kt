package com.example.musicapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(
    private var trackList: MutableList<TrackItemResponse> = mutableListOf(),
    private val onFavoriteToggle : (trackId: Long, position: Int) -> Unit,
    private val isFavorite : (trackId: Long) -> Boolean,
    private val onPlayClicked : (track: TrackItemResponse) -> Unit
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