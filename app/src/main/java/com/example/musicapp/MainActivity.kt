package com.example.musicapp

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
    private var allTracks: List<TrackItemResponse> = emptyList()

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


    private fun initComponents() {

        favoriteIds = mutableSetOf()

        favoriteManager = FavoriteManager(this)

        lifecycleScope.launch {
            favoriteManager.getFavorites().collect { ids ->
                val idsAsInt = ids.mapNotNull { it.toLongOrNull() }.toSet()
                favoriteIds.clear()
                favoriteIds.addAll(idsAsInt)
                trackAdapter.notifyDataSetChanged()

            }
        }

        genreChartIds = mapOf(
            "rock" to 152,
            "pop" to 132,
            "rap" to 116,
            "electro" to 106,
            "country" to 84,
            "movies" to 173,
            "latin" to 197
        )


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

        for (blurView in blurViews) {
            setupBlur(blurView)
        }

        binding.cvRock.setOnClickListener { searchByGenre("rock") }
        binding.cvLatin.setOnClickListener { searchByGenre("latin") }
        binding.cvPop.setOnClickListener { searchByGenre("pop") }
        binding.cvRap.setOnClickListener { searchByGenre("rap") }
        binding.cvCountry.setOnClickListener { searchByGenre("country") }
        binding.cvElectro.setOnClickListener { searchByGenre("electro") }
        binding.cvMovies.setOnClickListener { searchByGenre("movies") }
        binding.cvFav.setOnClickListener {
            val favoriteTracks = allTracks.filter { track ->
                favoriteIds.contains(track.id)
            }
            trackAdapter.updateList(favoriteTracks)
            binding.rvTracks.scrollToPosition(0)
        }



        binding.svSeachBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchByName(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?) = false

        })

        binding.svSeachBar.isIconified = false
        binding.svSeachBar.requestFocus()

        trackAdapter = TrackAdapter(
            mutableListOf(),
            onFavoriteToggle = { trackId, position ->
                toggleFavorite(trackId)
                trackAdapter.notifyItemChanged(position)
            },
            isFavorite = { trackId ->
                favoriteIds.contains(trackId)
            }
        )


        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = trackAdapter
        }

        homeByDefault()

    }


    private fun searchByName(name: String) {
        binding.progressBar.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            val myResponse = RetrofitInstance.api.getCancionesDeArtista(name)
            if (myResponse.isSuccessful) {
                Log.i("josantproject", "funciona")
                val track = myResponse.body()
                if (track != null) {
                    withContext(Dispatchers.Main) {
                        allTracks = track.data
                        trackAdapter.updateList(track.data)
                        binding.progressBar.isVisible = false

                    }
                }
            } else {
                Log.i("josantproject", "No funciona")
            }
        }

    }

    private fun homeByDefault() {
        binding.progressBar.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            val myResponse = RetrofitInstance.api.getChart()
            if (myResponse.isSuccessful) {
                val track = myResponse.body()
                if (track != null) {
                    withContext(Dispatchers.Main) {
                        allTracks = track.data
                        trackAdapter.updateList(track.data)
                        binding.progressBar.isVisible = false
                    }
                }
            }
        }
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
                        allTracks = track.data
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


}