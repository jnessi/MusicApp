package com.example.musicapp

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicapp.R
import com.example.musicapp.databinding.ActivityMainBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
   private lateinit var blurViews : List<BlurView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()

    }



    private fun initComponents(){

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

        for(blurView in blurViews){
            setupBlur(blurView)
        }

    }



    private fun setupBlur(blurView: BlurView) {
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurView.setupWith(rootView, RenderEffectBlur())
                .setBlurRadius(15f) // más fuerte
                .setBlurEnabled(true)
        } else {
            blurView.setBlurEnabled(false) // o usar un fallback
        }
    }



}