package com.linkpoint.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BuiltInThemesCompatibilityTest {

    @Test
    fun `linkpoint_default resolves`() {
        val resolved = BuiltInThemes.getById("linkpoint_default")
        assertNotNull(resolved)
        assertEquals("linkpoint_default", resolved?.id)
    }

    @Test
    fun `merged built-ins have no duplicate IDs`() {
        val builtIns = BuiltInThemes.getAllBuiltInThemes()
        val catalog = ThemeCatalog.allThemes()

        assertEquals(
            "BuiltInThemes.getAllBuiltInThemes() must have no duplicate IDs",
            builtIns.size,
            builtIns.map { it.id }.toSet().size,
        )
        assertEquals(
            "ThemeCatalog.allThemes() must have no duplicate IDs",
            catalog.size,
            catalog.map { it.id }.toSet().size,
        )

        val byIdInBuiltIns = builtIns.associateBy { it.id }
        for (theme in catalog) {
            val twin = byIdInBuiltIns[theme.id]
            if (twin != null) {
                assertEquals(
                    "Theme '${theme.id}' diverges between catalog and BuiltInThemes",
                    twin,
                    theme,
                )
            }
        }
    }
}
