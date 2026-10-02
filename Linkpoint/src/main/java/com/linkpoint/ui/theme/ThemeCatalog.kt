package com.linkpoint.ui.theme

/**
 * Catalog of built-in themes grouped by visual family.
 */
object ThemeCatalog {
    enum class ThemeFamily(val displayName: String) {
        LINKPOINT("Linkpoint"),
        TERMINAL_NEON("Terminal & Neon"),
        CONSOLE_AMBER("Console & Amber"),
        METAL_JEWEL("Metal & Jewel"),
        DAYLIGHT("Daylight"),
        VIEWER_INSPIRED("Viewer-Inspired"),
        COMMUNITY("Community & Sci-Fi")
    }

    private val familyToThemes: Map<ThemeFamily, List<ThemePack>> = mapOf(
        ThemeFamily.LINKPOINT to listOf(BuiltInThemes.LINKPOINT_DEFAULT),
        ThemeFamily.TERMINAL_NEON to listOf(
            BuiltInThemes.INK_TERMINAL,
            BuiltInThemes.NEO_NOIR_NEON,
            BuiltInThemes.METRO_CYAN,
            BuiltInThemes.AURORA_GLASS_NIGHT,
            BuiltInThemes.SLATE_CYAN
        ),
        ThemeFamily.CONSOLE_AMBER to listOf(
            BuiltInThemes.LCARS_TNG,
            BuiltInThemes.MIDNIGHT_AMBER,
            BuiltInThemes.ROYAL_BRONZE,
            BuiltInThemes.FOREST_COPPER,
            BuiltInThemes.OBSIDIAN_CRIMSON
        ),
        ThemeFamily.METAL_JEWEL to listOf(
            BuiltInThemes.NAVY_GOLD,
            BuiltInThemes.ART_DECO,
            BuiltInThemes.EMERALD_SILVER,
            BuiltInThemes.ROYAL_SILVER,
            BuiltInThemes.DEEP_PURPLE_PLATINUM,
            BuiltInThemes.CHARCOAL_CHAMPAGNE,
            BuiltInThemes.SLATE_GUNMETAL,
            BuiltInThemes.ROSE_GOLD,
            BuiltInThemes.BURGUNDY_ROSEGOLD,
            BuiltInThemes.CLEVERFERRET_GOLD
        ),
        ThemeFamily.DAYLIGHT to listOf(
            BuiltInThemes.FRUTIGER_AERO,
            BuiltInThemes.PAPER_INK,
            BuiltInThemes.ART_NOUVEAU,
            BuiltInThemes.CALM_CLINICAL,
            BuiltInThemes.SOLARPUNK_CIVIC
        ),
        ThemeFamily.VIEWER_INSPIRED to listOf(
            BuiltInThemes.SL_CLASSIC,
            BuiltInThemes.FIRESTORM
        ),
        ThemeFamily.COMMUNITY to listOf(
            BuiltInThemes.STARGATE_ATLANTIS,
            BuiltInThemes.STARGATE_SG1
        )
    )

    fun allThemes(): List<ThemePack> = familyToThemes.values.flatten()

    fun families(): Set<ThemeFamily> = familyToThemes.keys

    fun themesInFamily(family: ThemeFamily): List<ThemePack> = familyToThemes[family].orEmpty()

    fun familyForTheme(themeId: String): ThemeFamily? =
        familyToThemes.entries.firstOrNull { (_, themes) -> themes.any { it.id == themeId } }?.key

    fun getById(id: String): ThemePack? = allThemes().firstOrNull { it.id == id }
}
