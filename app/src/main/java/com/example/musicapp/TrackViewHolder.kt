package com.example.musicapp


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.ActivityItemBinding
import com.squareup.picasso.Picasso

class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ActivityItemBinding.bind(view)

    fun bind(
        track: TrackItemResponse,
        position: Int,
        isFav: Boolean,
        onFavoriteToggle: (trackId: Long, position: Int) -> Unit
    ) {

        binding.tvSongName.text = track.title
        binding.tvRanking.text = track.rank.toString()
        binding.tvDuration.text = track.duration.toString()
        binding.tvArtistName.text = track.artist.name
        Picasso.get().load(track.album.cover).into(binding.imageCoverAlbum)

        val favIcon = if(isFav){
            R.drawable.ic_fav_filled
        }else{
            R.drawable.ic_fav_empty
        }

        binding.imageFavSong.setImageResource(favIcon)

        binding.imageFavSong.setOnClickListener {
            onFavoriteToggle(track.id,position)
        }




    }

}