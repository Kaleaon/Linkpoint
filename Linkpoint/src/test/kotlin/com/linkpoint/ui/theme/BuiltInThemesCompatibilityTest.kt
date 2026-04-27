package com.linkpoint.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BuiltInThemesCompatibilityTest {

    @Test
    fun `linkpoint_default resolves`() {
        val resolved = BuiltInThemes.getById("linkpoint_default")
        assertNotNull(resolved)
        assertEquals(BuiltInThemes.LINKPOINT_DEFAULT.id, resolved?.id)
    }

    @Test
    fun `merged built-ins have no duplicate IDs`() {
        val mergedIds = (BuiltInThemes.getAllBuiltInThemes() + ThemeCatalog.allThemes())
            .map { it.id }

        assertEquals(
            "Merged built-in IDs must be unique",
            mergedIds.size,
            mergedIds.toSet().size
        )
    }
}
