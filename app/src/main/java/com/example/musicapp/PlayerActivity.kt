package com.example.musicapp

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.databinding.ActivityPlayerBinding
import com.squareup.picasso.Picasso
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var progressJob: Job? = null
    private lateinit var blurViews: List<BlurView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()

    }

    private fun initComponents() {

        blurViews = listOf(
            binding.blurPlayerBack,
            binding.blurPlayerCover

        )

        for (blurView in blurViews) {
            setupBlur(blurView)
        }

        // Retrieve and display song metadata passed via intent
        val duration = 30
        binding.tvTimeStart.text = "00:00"
        binding.tvDetailsArtistName.text = intent.getStringExtra("artist") ?: ""
        binding.tvDetailsTrackName.text = intent.getStringExtra("title") ?: ""
        binding.tvTimeEnd.text = duration.toMinSecFormat()
        Picasso.get().load(intent.getStringExtra("coverUrl")).into(binding.ivPlayerCover)

        // Initialize and prepare media player with song preview URL
        mediaPlayer = MediaPlayer().apply {
            setDataSource(intent.getStringExtra("previewUrl"))
            prepare()
        }

        binding.sbTrackBar.max = duration

        // Handle play/pause button functionality
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                binding.iconPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()

                // Cancel any existing progress update job
                progressJob?.cancel()

                // Launch coroutine to update progress every second
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

        // Allow users to seek through the track
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

        // Reset UI when preview finishes playing
        mediaPlayer?.setOnCompletionListener {
            isPlaying = false
            binding.iconPlayPause.setImageResource(R.drawable.ic_play)
            binding.sbTrackBar.progress = 0
            binding.tvTimeStart.text = "00:00"
        }

        // Handle back button to finish the activity
        binding.cvBackButton.setOnClickListener {
            finish()
        }

    }

    // Clean up resources when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        progressJob?.cancel()
    }

    private fun setupBlur(blurView: BlurView) {
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setBlurRadius(15f)
                .setBlurEnabled(true)
        } else {
            blurView.setBlurEnabled(false)
        }
    }


}