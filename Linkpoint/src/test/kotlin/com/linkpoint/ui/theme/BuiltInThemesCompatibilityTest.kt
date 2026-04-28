package com.linkpoint.ui.theme

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class BuiltInThemesCompatibilityTest {

    @Test
    fun `linkpoint_default resolves`() {
        val resolved = BuiltInThemes.getById("linkpoint_default")
        assertNotNull(resolved)
        assertEquals(BuiltInThemes.LINKPOINT_DEFAULT.id, resolved?.id)
    }

    @Test
    fun `merged built-ins have no duplicate IDs`() {
        // BuiltInThemes.getAllBuiltInThemes() and ThemeCatalog.allThemes()
        // intentionally surface the same theme objects under two different
        // entry points (legacy list + family catalog), so the *combined*
        // list will have repeats. What we actually want to assert is:
        //   1. neither list has internal duplicates (a typo'd id would
        //      collapse two themes into one), and
        //   2. when one source declares a theme id, the other source
        //      points at the same theme.
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
