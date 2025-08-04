package com.example.musicapp

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.databinding.ActivityPlayerBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var progressJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()

    }

    private fun initComponents() {

        val duration = 30

        binding.tvTimeStart.text = "00:00"
        binding.tvDetailsArtistName.text = intent.getStringExtra("artist") ?: ""
        binding.tvDetailsTrackName.text = intent.getStringExtra("title") ?: ""
        binding.tvTimeEnd.text = duration.toMinSecFormat()
        Picasso.get().load(intent.getStringExtra("coverUrl")).into(binding.ivPlayerCover)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(intent.getStringExtra("previewUrl"))
            prepare()
        }

        binding.sbTrackBar.max = duration

        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                binding.iconPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()

                progressJob?.cancel()

                progressJob = lifecycleScope.launch {
                    while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val currentPosition = mediaPlayer!!.currentPosition / 1000
                        binding.sbTrackBar.progress = currentPosition
                        binding.tvTimeStart.text = currentPosition.toMinSecFormat()
                        delay(1000)
                    }
                }
                binding.iconPlayPause.setImageResource(R.drawable.ic_pause)
            }
            isPlaying = !isPlaying
        }

        binding.sbTrackBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * 1000)
                    binding.tvTimeStart.text = progress.toMinSecFormat()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            binding.iconPlayPause.setImageResource(R.drawable.ic_play)
            binding.sbTrackBar.progress = 0
            binding.tvTimeStart.text = "00:00"
        }

        binding.cvBackButton.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()
    }


}