package com.ayham.tv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ayham.tv.data.AppInfo
import com.ayham.tv.data.AppRepository
import com.ayham.tv.data.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val allApps: List<AppInfo> = emptyList(),
    val favorites: List<AppInfo> = emptyList(),
    val recents: List<AppInfo> = emptyList(),
    val favoritePackages: Set<String> = emptySet(),
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val prefs = Prefs(application)
    private val installed = MutableStateFlow<List<AppInfo>>(emptyList())

    val ui: StateFlow<HomeUiState> = combine(installed, prefs.favorites, prefs.recents) { apps, favs, recents ->
        val byPackage = apps.associateBy { it.packageName }
        HomeUiState(
            allApps = apps,
            favorites = favs.mapNotNull { byPackage[it] },
            recents = recents.mapNotNull { byPackage[it] },
            favoritePackages = favs.toSet(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun refresh() {
        viewModelScope.launch { installed.value = repository.loadApps() }
    }

    /** Returns true if the app started. */
    fun launch(app: AppInfo): Boolean {
        val started = repository.launch(app.packageName)
        if (started) viewModelScope.launch { prefs.addRecent(app.packageName) }
        return started
    }

    /** Calls [onResult] with true if the app is now a favorite. */
    fun toggleFavorite(app: AppInfo, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(prefs.toggleFavorite(app.packageName)) }
    }
}
