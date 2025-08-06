package com.example.musicapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityMainBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

// Retrofit singleton instance for making API requests
    private const val BASE_URL = "https://api.deezer.com/"

    val api: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var blurViews: List<BlurView>
    private lateinit var retrofit: Retrofit
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var genreChartIds: Map<String, Int>
    private lateinit var favoriteIds: MutableSet<Long>

    private lateinit var favoriteManager: FavoriteManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()


    }

    // Initializes all UI components and sets up listeners
    private fun initComponents() {

        favoriteIds = mutableSetOf()

        favoriteManager = FavoriteManager(this)

        // Observe favorite songs from DataStore and update UI when changed
        lifecycleScope.launch {
            favoriteManager.getFavorites().collect { ids ->
                val idsAsInt = ids.mapNotNull { it.toLongOrNull() }.toSet()
                favoriteIds.clear()
                favoriteIds.addAll(idsAsInt)
                trackAdapter.notifyDataSetChanged()

            }
        }

        // Deezer genre IDs mapped to custom labels
        genreChartIds = mapOf(
            "rock" to 152,
            "pop" to 132,
            "rap" to 116,
            "electro" to 106,
            "country" to 84,
            "movies" to 173,
            "latin" to 197
        )


        // Reference to all blur-enabled views
        blurViews = listOf(
            binding.blurRock,
            binding.blurPop,
            binding.blurRap,
            binding.blurCountry,
            binding.blurLatin,
            binding.blurMovies,
            binding.blurElectro,
            binding.blurFav
        )

        // Apply blur effect to supported views
        for (blurView in blurViews) {
            setupBlur(blurView)
        }

        // Set click listeners for genre filters
        binding.cvRock.setOnClickListener { searchByGenre("rock") }
        binding.cvLatin.setOnClickListener { searchByGenre("latin") }
        binding.cvPop.setOnClickListener { searchByGenre("pop") }
        binding.cvRap.setOnClickListener { searchByGenre("rap") }
        binding.cvCountry.setOnClickListener { searchByGenre("country") }
        binding.cvElectro.setOnClickListener { searchByGenre("electro") }
        binding.cvMovies.setOnClickListener { searchByGenre("movies") }
        binding.cvFav.setOnClickListener {
            showFavoriteTracks()
        }


        // SearchView text submission logic
        binding.svSeachBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchByName(query.orEmpty())
                return false
            }
            override fun onQueryTextChange(newText: String?) = false

        })

        // Force SearchView to be expanded on launch
        binding.svSeachBar.isIconified = false
        binding.svSeachBar.requestFocus()

        // Initialize the RecyclerView adapter with lambda callbacks
        trackAdapter = TrackAdapter(
            mutableListOf(),
            onFavoriteToggle = { trackId, position ->
                toggleFavorite(trackId)
                trackAdapter.notifyItemChanged(position)
            },
            isFavorite = { trackId ->
                favoriteIds.contains(trackId)
            },
            onPlayClicked = { track ->
                openPlayer(track)
            }
        )

        // Setup RecyclerView
        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = trackAdapter
        }

        // Load home content by default
        homeByDefault()

    }


    // Fetches tracks by artist name
    private fun searchByName(name: String) {
        binding.progressBar.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            val myResponse = RetrofitInstance.api.getTrackByArtist(name)
            if (myResponse.isSuccessful) {
                Log.i("josantproject", "funciona")
                val track = myResponse.body()
                if (track != null) {
                    withContext(Dispatchers.Main) {
                        trackAdapter.updateList(track.data)
                        binding.progressBar.isVisible = false

                    }
                }
            } else {
                Log.i("josantproject", "No funciona")
            }
        }

    }

    // Loads the top chart tracks on app launch
    private fun homeByDefault() {
        binding.progressBar.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            val myResponse = RetrofitInstance.api.getChart()
            if (myResponse.isSuccessful) {
                val track = myResponse.body()
                if (track != null) {
                    withContext(Dispatchers.Main) {
                        trackAdapter.updateList(track.data)
                        binding.progressBar.isVisible = false
                    }
                }
            }
        }
    }


    // Applies a blur effect to views (only supported on Android 12+)
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


    // Fetches and displays songs from a specific genre
    private fun searchByGenre(genre: String) {

        val key = genre.trim().lowercase()
        val genreId = genreChartIds[key] ?: 0

        binding.progressBar.isVisible = true

        lifecycleScope.launch(Dispatchers.IO) {
            val myResponse = RetrofitInstance.api.getChart(genreId)
            if (myResponse.isSuccessful) {
                val track = myResponse.body()
                if (track != null) {
                    withContext(Dispatchers.Main) {
                        trackAdapter.updateList(track?.data ?: emptyList())
                        binding.rvTracks.scrollToPosition(0)
                        binding.progressBar.isVisible = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.isVisible = false
                    }
                }
            }

        }
    }

    // Toggles favorite state of a track and updates the local cache
    private fun toggleFavorite(trackId: Long) {

        lifecycleScope.launch {
            favoriteManager.toggleFavoriteData(trackId)
        }

        if (favoriteIds.contains(trackId)) {
            favoriteIds.remove(trackId)
        } else {
            favoriteIds.add(trackId)
        }

    }

    // Launches PlayerActivity with track details via intent extras
    private fun openPlayer(track: TrackItemResponse) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("title", track.title)
            putExtra("artist", track.artist.name)
            putExtra("coverUrl", track.album.cover)
            putExtra("previewUrl", track.preview)
            putExtra("duration", track.duration)
        }
        startActivity(intent)
    }

    // Retrieves favorite tracks by ID from the API and displays them
    private fun showFavoriteTracks() {

        binding.progressBar.isVisible = true

        lifecycleScope.launch {
            val favoriteTracks = mutableListOf<TrackItemResponse>()

            for (id in favoriteIds) {
                val response = RetrofitInstance.api.getTrackById(id)
                if (response.isSuccessful) {
                    response.body()?.let { favoriteTracks.add(it) }
                }
            }

            withContext(Dispatchers.Main) {
                trackAdapter.updateList(favoriteTracks)
                binding.rvTracks.scrollToPosition(0)
                binding.progressBar.isVisible = false
            }
        }

    }


}