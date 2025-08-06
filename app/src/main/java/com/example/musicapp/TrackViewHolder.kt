package com.example.musicapp


import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.ActivityItemBinding
import com.squareup.picasso.Picasso

// Extension function to convert seconds into MM:SS format
fun Int.toMinSecFormat(): String{
    val minutes = this/60
    val seconds = this%60
    return String.format("%d:%02d", minutes, seconds)
}


class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ActivityItemBinding.bind(view)

    fun bind(
        track: TrackItemResponse,
        position: Int,
        isFav: Boolean,
        onFavoriteToggle: (trackId: Long, position: Int) -> Unit,
        onPlayClicked : (track: TrackItemResponse) -> Unit
    ) {

        // Populate text views with track info
        binding.tvSongName.text = track.title
        binding.tvRanking.text = track.rank.toString()
        binding.tvDuration.text = track.duration.toMinSecFormat()
        binding.tvArtistName.text = track.artist.name
        Picasso.get().load(track.album.cover).into(binding.imageCoverAlbum) // Load album cover using Picasso

        // Display the correct icon depending on whether the track is a favorite
        val favIcon = if(isFav){
            R.drawable.ic_fav_filled
        }else{
            R.drawable.ic_fav_empty
        }

        binding.imageFavSong.setImageResource(favIcon)

        // Handle click to toggle favorite status
        binding.imageFavSong.setOnClickListener {
            onFavoriteToggle(track.id,position)
        }

        // Handle click to launch the player
        binding.cvPlayButtom.setOnClickListener {
            onPlayClicked(track)
        }







    }

}