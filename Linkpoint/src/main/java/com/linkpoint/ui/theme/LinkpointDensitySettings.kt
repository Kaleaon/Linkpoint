package com.linkpoint.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.densityDataStore by preferencesDataStore(name = "linkpoint_ui_density")

class DensitySettingsStore(private val context: Context) {
    private val densityKey: Preferences.Key<String> = stringPreferencesKey("density_mode")

    val densityMode: Flow<ThemePack.DensityMode> = context.densityDataStore.data.map { prefs ->
        prefs[densityKey]
            ?.let { runCatching { ThemePack.DensityMode.valueOf(it) }.getOrNull() }
            ?: ThemePack.DensityMode.BALANCED
    }

    suspend fun setDensityMode(mode: ThemePack.DensityMode) {
        context.densityDataStore.edit { prefs ->
            prefs[densityKey] = mode.name
        }
    }
}

val LocalDensityMode = staticCompositionLocalOf { ThemePack.DensityMode.BALANCED }

object LinkpointDensity {
    val current: ThemePack.DensityMode
        @Composable
        @ReadOnlyComposable
        get() = LocalDensityMode.current
}
