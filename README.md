# 🎵 MusicApp

**MusicApp** is a modern music streaming preview app built with **Kotlin** and **Android Studio**, powered by the [Deezer API](https://www.deezer.com). Users can search for songs, play 30-second previews, view track details, and manage their favorite tracks using `DataStore`.

## 🚀 Features

- 🔍 Search for songs by name or genre
- 🎧 Preview player with play/pause, time tracking, and seek bar
- ❤️ Add/remove tracks from a persistent favorites list using `DataStore`
- 📊 Display of song rank and duration
- 🧊 Modern UI with **glassmorphism** using `BlurView`
- 🔄 Loading state management with `ProgressBar`
- 🌐 API integration with Retrofit and Coroutines

## 🛠️ Built With

- Kotlin
- Android SDK
- Retrofit + Gson
- Kotlin Coroutines + LifecycleScope
- DataStore (Jetpack)
- Picasso (image loading)
- ConstraintLayout & MaterialCardView
- BlurView (glassmorphism effect)
- Deezer REST API
  
---

## 📂 Project Structure

MainActivity: Home screen with search, genre filter, and RecyclerView

PlayerActivity: Preview player with media controls

TrackAdapter: RecyclerView adapter with favorite toggle and play action

FavoriteManager: Utility class to manage favorites with DataStore

RetrofitInstance & ApiService: Handle API requests
