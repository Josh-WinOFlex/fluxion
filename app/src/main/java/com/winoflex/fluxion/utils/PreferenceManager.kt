package com.winoflex.fluxion.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val DARK_MODE = intPreferencesKey("dark_mode") // 0: System, 1: Dark, 2: Light
        val USE_DYNAMIC_COLORS = booleanPreferencesKey("use_dynamic_colors")
    }

    val darkModeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: 0
        }

    val useDynamicColorsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[USE_DYNAMIC_COLORS] ?: true
        }

    suspend fun setDarkMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = mode
        }
    }

    suspend fun setUseDynamicColors(use: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USE_DYNAMIC_COLORS] = use
        }
    }
}
