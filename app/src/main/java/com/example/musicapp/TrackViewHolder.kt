package com.example.musicapp


import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.databinding.ActivityItemBinding
import com.squareup.picasso.Picasso

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

        binding.tvSongName.text = track.title
        binding.tvRanking.text = track.rank.toString()
        binding.tvDuration.text = track.duration.toMinSecFormat()
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

        binding.cvPlayButtom.setOnClickListener {
            onPlayClicked(track)
        }







    }

}