package com.linkpoint.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCatalogTest {

    @Test
    fun `all catalog themes have unique IDs`() {
        val ids = ThemeCatalog.allThemes().map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `catalog covers every family`() {
        ThemeCatalog.ThemeFamily.entries.forEach { family ->
            assertTrue("Expected family $family to have at least one theme", ThemeCatalog.themesInFamily(family).isNotEmpty())
        }
    }

    @Test
    fun `lookup behavior returns expected themes and families`() {
        val theme = ThemeCatalog.getById(BuiltInThemes.LINKPOINT_DEFAULT.id)
        assertNotNull(theme)
        assertEquals(BuiltInThemes.LINKPOINT_DEFAULT.id, theme?.id)
        assertEquals(
            ThemeCatalog.ThemeFamily.LINKPOINT,
            ThemeCatalog.familyForTheme(BuiltInThemes.LINKPOINT_DEFAULT.id)
        )
        assertEquals(null, ThemeCatalog.getById("missing_theme"))
        assertEquals(null, ThemeCatalog.familyForTheme("missing_theme"))
    }
}
