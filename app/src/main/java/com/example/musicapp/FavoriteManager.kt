package com.example.musicapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create a singleton instance of DataStore for preferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoriteManager(private val context: Context) {

    private val dataStore = context.dataStore

    // Key to store the set of favorite track IDs
    companion object {
        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_tracks")
    }

    // Adds or removes a track from the set of favorites based on its current status
    suspend fun toggleFavoriteData(trackId: Long) {

        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            val mutableFavorites = currentFavorites.toMutableSet()

            val idAsString = trackId.toString()
            if (mutableFavorites.contains(idAsString)) {
                mutableFavorites.remove(idAsString) // If already favorite, remove it
            } else {
                mutableFavorites.add(idAsString) // If not, add it
            }

            preferences[FAVORITES_KEY] = mutableFavorites // Save updated set
        }

    }

    // Returns a flow of the current set of favorite track IDs
    fun getFavorites(): Flow<Set<String>> {

        return dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    }


}