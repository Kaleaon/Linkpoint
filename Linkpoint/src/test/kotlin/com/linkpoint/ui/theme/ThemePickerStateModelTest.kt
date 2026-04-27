package com.linkpoint.ui.theme

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePickerStateModelTest {

    @Test
    fun `groups built-ins by family and supports query filtering`() = runTest {
        val gateway = FakeGateway(
            themes = listOf(
                BuiltInThemes.LINKPOINT_DEFAULT,
                BuiltInThemes.SL_CLASSIC,
                BuiltInThemes.CLEVERFERRET_GOLD,
                sampleUserTheme()
            )
        )
        val model = ThemePickerStateModel(gateway, this)

        val grouped = model.uiState.value.filteredBuiltInThemesByFamily
        assertEquals(1, grouped[ThemeCatalog.Family.LINKPOINT]?.size)
        assertEquals(1, grouped[ThemeCatalog.Family.VIEWER_INSPIRED]?.size)
        assertEquals(1, grouped[ThemeCatalog.Family.METALLIC_DARK]?.size)

        model.onSearchQueryChanged("classic")
        val filtered = model.uiState.value.filteredBuiltInThemesByFamily
        assertEquals(listOf(BuiltInThemes.SL_CLASSIC.id), filtered.values.flatten().map { it.id })
    }

    @Test
    fun `tracks recently used themes`() = runTest {
        val themes = listOf(BuiltInThemes.LINKPOINT_DEFAULT, BuiltInThemes.FIRESTORM, sampleUserTheme())
        val gateway = FakeGateway(themes)
        val model = ThemePickerStateModel(gateway, this)

        model.onThemeSelected(BuiltInThemes.FIRESTORM)
        model.onThemeSelected(sampleUserTheme())

        val recent = model.uiState.value.recentlyUsedThemeIds
        assertEquals(listOf("user_theme", BuiltInThemes.FIRESTORM.id, BuiltInThemes.LINKPOINT_DEFAULT.id), recent)
    }

    @Test
    fun `delete exposes undo candidate`() = runTest {
        val theme = sampleUserTheme()
        val gateway = FakeGateway(listOf(BuiltInThemes.LINKPOINT_DEFAULT, theme))
        val model = ThemePickerStateModel(gateway, this)

        model.requestDelete(theme)
        model.confirmDeleteTheme()

        assertEquals(theme.id, model.uiState.value.deletedThemeUndoCandidate?.id)
        assertFalse(model.uiState.value.bannerIsError)
        assertTrue(model.uiState.value.bannerMessage?.contains("Deleted") == true)
    }

    private class FakeGateway(themes: List<ThemePack>) : ThemePickerThemeGateway {
        override val availableThemes: MutableStateFlow<List<ThemePack>> = MutableStateFlow(themes)
        override val activeTheme: MutableStateFlow<ThemePack> = MutableStateFlow(BuiltInThemes.LINKPOINT_DEFAULT)

        override fun setActiveTheme(theme: ThemePack) {
            activeTheme.value = theme
        }

        override suspend fun saveTheme(theme: ThemePack): Result<ThemePack> = Result.success(theme)

        override suspend fun deleteTheme(themeId: String): Boolean {
            availableThemes.value = availableThemes.value.filterNot { it.id == themeId }
            return true
        }

        override suspend fun importTheme(json: String): Result<ThemePack> = Result.failure(UnsupportedOperationException())

        override suspend fun exportTheme(theme: ThemePack): Result<Uri> = Result.success(Uri.parse("content://theme/${theme.id}"))

        override fun createNewTheme(name: String, description: String, author: String, baseTheme: ThemePack): ThemePack {
            return baseTheme.copy(id = "new_theme", name = name, description = description, isBuiltIn = false)
        }

        override fun duplicateTheme(theme: ThemePack): ThemePack = theme.copy(id = "copy_theme")
    }
}

private fun sampleUserTheme() = ThemePack(
    id = "user_theme",
    name = "My Theme",
    description = "custom",
    author = "Tester",
    isBuiltIn = false,
    colorPrimary = "#111111",
    colorPrimaryDark = "#000000",
    colorOnPrimary = "#FFFFFF",
    colorSecondary = "#222222",
    colorOnSecondary = "#FFFFFF",
    colorBackground = "#121212",
    colorSurface = "#1E1E1E",
    colorOnSurface = "#FFFFFF",
    colorOnSurfaceVariant = "#BBBBBB"
)
