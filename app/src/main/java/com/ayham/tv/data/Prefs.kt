package com.ayham.tv.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "ayham_tv")

/** Stores favorites and recently launched apps (as ordered package-name lists). */
class Prefs(private val context: Context) {

    private val favoritesKey = stringPreferencesKey("favorites")
    private val recentsKey = stringPreferencesKey("recents")

    val favorites: Flow<List<String>> = context.dataStore.data.map { it.list(favoritesKey) }
    val recents: Flow<List<String>> = context.dataStore.data.map { it.list(recentsKey) }

    suspend fun toggleFavorite(packageName: String): Boolean {
        var nowFavorite = false
        context.dataStore.edit { prefs ->
            val current = prefs.list(favoritesKey).toMutableList()
            if (current.remove(packageName)) {
                nowFavorite = false
            } else {
                current.add(packageName)
                nowFavorite = true
            }
            prefs[favoritesKey] = current.joinToString(SEPARATOR)
        }
        return nowFavorite
    }

    suspend fun addRecent(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs.list(recentsKey).toMutableList()
            current.remove(packageName)
            current.add(0, packageName)
            prefs[recentsKey] = current.take(MAX_RECENTS).joinToString(SEPARATOR)
        }
    }

    private fun Preferences.list(key: Preferences.Key<String>): List<String> =
        (this[key] ?: "").split(SEPARATOR).filter { it.isNotBlank() }

    private companion object {
        const val SEPARATOR = ","
        const val MAX_RECENTS = 12
    }
}
