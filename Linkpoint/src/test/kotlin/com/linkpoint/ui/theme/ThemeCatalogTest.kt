package com.linkpoint.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeCatalogTest {

    @Test
    fun `all catalog themes have unique IDs`() {
        val ids = ThemeCatalog.allThemes().map { it.id }
        assertTrue("Catalog should contain at least 28 themes", ids.size >= 28)
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
        val theme = ThemeCatalog.getById("linkpoint_default")
        assertNotNull(theme)
        assertEquals("linkpoint_default", theme?.id)
        assertEquals(
            ThemeCatalog.ThemeFamily.LINKPOINT,
            ThemeCatalog.familyForTheme("linkpoint_default")
        )
        assertEquals(null, ThemeCatalog.getById("missing_theme"))
        assertEquals(null, ThemeCatalog.familyForTheme("missing_theme"))
    }

    @Test
    fun `all 26 central themes and 4 community themes are present`() {
        val allIds = ThemeCatalog.allThemes().map { it.id }.toSet()

        val expectedCentral = listOf(
            "art-deco", "art-nouveau", "aurora-glass-night", "burgundy-rose-gold",
            "calm-clinical", "charcoal-champagne", "cleverferret_gold", "deep-purple-platinum",
            "emerald-silver", "firestorm", "forest-copper", "frutiger-aero",
            "ink-terminal-modern", "linkpoint_default", "midnight-amber", "navy-gold",
            "neo-noir-neon", "obsidian-crimson", "paper-ink", "rose-gold",
            "royal-bronze", "royal-silver", "sl_classic", "slate-cyan",
            "slate-gunmetal", "solarpunk-civic"
        )

        val expectedCommunity = listOf(
            "lcars-tng", "metro-cyan", "stargate-atlantis", "stargate-sg1"
        )

        for (id in expectedCentral) {
            assertTrue("Missing central theme ID: $id", allIds.contains(id))
        }

        for (id in expectedCommunity) {
            assertTrue("Missing community theme ID: $id", allIds.contains(id))
        }
    }

    @Test
    fun `canonical ktheme models preserve color scheme tokens without loss`() {
        val kthemes = ThemeCatalog.allKthemes()
        assertTrue("Expected non-empty canonical Theme list", kthemes.isNotEmpty())

        val artDeco = kthemes.find { it.metadata.id == "art-deco" }
        assertNotNull("art-deco theme should be present in canonical model list", artDeco)

        val colorScheme = artDeco!!.colorScheme
        assertNotNull(colorScheme.primary)
        assertNotNull(colorScheme.tertiary)
        assertNotNull(colorScheme.outline)
        assertNotNull(colorScheme.scrim)

        val m3Scheme = artDeco.colorScheme.toMaterial3ColorScheme()
        assertNotNull(m3Scheme)
    }

    @Test
    fun `legacy ThemePack custom themes convert losslessly`() {
        val customPack = ThemePack.createTemplate()
        val jsonString = customPack.toJson()
        val deserialized = ThemePack.fromJson(jsonString)

        assertNotNull("Legacy ThemePack JSON should deserialize cleanly", deserialized)
        assertEquals(customPack.id, deserialized?.id)

        val ktheme = deserialized!!.toKthemeTheme()
        assertEquals(customPack.colorPrimary, ktheme.colorScheme.primary)

        val m3Scheme = deserialized.toMaterial3ColorScheme()
        assertNotNull(m3Scheme)
    }
}
