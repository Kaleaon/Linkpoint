package com.linkpoint.ui.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ktheme.library.KthemeAPI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ThemeManagerTest {

    private lateinit var context: Context
    private lateinit var themeManager: ThemeManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        ThemeCatalog.resetCache()
        themeManager = ThemeManager.getInstance(context)
    }

    @Test
    fun `ThemeCatalog resetCache purges static cache`() {
        val initialThemes = ThemeCatalog.allThemes(context)
        assertTrue("Catalog should contain initial themes", initialThemes.isNotEmpty())

        ThemeCatalog.resetCache()

        val refreshedThemes = ThemeCatalog.allThemes(context)
        assertTrue("Refreshed catalog should contain themes", refreshedThemes.isNotEmpty())
        assertNotSame("Resetting cache must yield new ThemePack instances", initialThemes, refreshedThemes)
    }

    @Test
    fun `refreshThemesFromKtheme purges ThemeCatalog cache and reloads available themes`() {
        val initialAvailable = themeManager.availableThemes.value
        assertTrue("Initial available themes should not be empty", initialAvailable.isNotEmpty())

        // Warm up ThemeCatalog cache
        val cachedPacksBefore = ThemeCatalog.allThemes(context)

        // Trigger refresh
        themeManager.refreshThemesFromKtheme()

        val cachedPacksAfter = ThemeCatalog.allThemes(context)
        assertNotSame("refreshThemesFromKtheme must invalidate ThemeCatalog static cache", cachedPacksBefore, cachedPacksAfter)
    }

    @Test
    fun `refreshThemesFromKtheme re-emits updated ThemePack on activeTheme StateFlow`() {
        val defaultTheme = BuiltInThemes.LINKPOINT_DEFAULT
        themeManager.setActiveTheme(defaultTheme)

        val initialActive = themeManager.activeTheme.value
        assertEquals(defaultTheme.id, initialActive.id)

        // Publish an updated version of the default theme to KthemeAPI with a modified primary color
        val updatedKtheme = defaultTheme.toKthemeTheme().let { ktheme ->
            ktheme.copy(
                colorScheme = ktheme.colorScheme.copy(
                    primary = "#FF123456"
                )
            )
        }
        KthemeAPI.shareTheme(updatedKtheme)

        // Trigger refresh from Ktheme watcher callback
        themeManager.refreshThemesFromKtheme()

        val updatedActive = themeManager.activeTheme.value
        assertEquals(defaultTheme.id, updatedActive.id)
        assertEquals("#FF123456", updatedActive.colorPrimary)
        assertNotEquals("Active theme primary color must reflect update", initialActive.colorPrimary, updatedActive.colorPrimary)
    }
}
