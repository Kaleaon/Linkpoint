package com.ktheme.library

import com.ktheme.models.Theme
import java.util.concurrent.CopyOnWriteArrayList

interface ThemeChangeListener {
    fun onThemeAdded(theme: Theme)
    fun onThemeRemoved(themeId: String)
    fun onThemeUpdated(theme: Theme)
}

/**
 * Local no-op fallback for Ktheme integration.
 * Keeps theme sharing APIs functional even when the remote Ktheme dependency is unavailable.
 */
object KthemeAPI {
    private val themes = linkedMapOf<String, Theme>()
    private val listeners = CopyOnWriteArrayList<ThemeChangeListener>()

    fun getAvailableThemes(): List<Theme> = themes.values.toList()

    fun shareTheme(theme: Theme) {
        val existed = themes.containsKey(theme.metadata.id)
        themes[theme.metadata.id] = theme
        listeners.forEach { listener ->
            if (existed) listener.onThemeUpdated(theme) else listener.onThemeAdded(theme)
        }
    }

    fun removeTheme(themeId: String) {
        if (themes.remove(themeId) != null) {
            listeners.forEach { it.onThemeRemoved(themeId) }
        }
    }

    fun onThemeChanged(listener: ThemeChangeListener) {
        listeners.add(listener)
    }
}
