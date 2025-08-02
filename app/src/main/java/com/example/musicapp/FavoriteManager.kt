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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name ="favorites")

class FavoriteManager (private val context: Context){

    private val dataStore = context.dataStore

    companion object{
        private val FAVORITES_KEY = stringSetPreferencesKey("favorite_tracks")
    }

    suspend fun toggleFavoriteData(trackId:Long){

        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY] ?: emptySet()
            val mutableFavorites = currentFavorites.toMutableSet()

            val idAsString = trackId.toString()
            if(mutableFavorites.contains(idAsString)){
                mutableFavorites.remove(idAsString)
            }else{
                mutableFavorites.add(idAsString)
            }

            preferences[FAVORITES_KEY] = mutableFavorites
        }

    }

    fun getFavorites() : Flow<Set<String>> {

        return dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    }



}