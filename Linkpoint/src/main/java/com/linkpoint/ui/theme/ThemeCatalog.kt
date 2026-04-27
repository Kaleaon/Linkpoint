package com.linkpoint.ui.theme

/**
 * Catalog of built-in themes grouped by visual family.
 */
object ThemeCatalog {
    /**
     * Legacy family enum used by Theme Picker state model and UI grouping chips.
     */
    enum class Family(val displayName: String) {
        LINKPOINT("Linkpoint"),
        VIEWER_INSPIRED("Viewer Inspired"),
        METALLIC_DARK("Metallic Dark"),
        COMMUNITY("Community")
    }

    /**
     * Family enum used by ThemeCatalog regression and compatibility tests.
     */
    enum class ThemeFamily {
        LINKPOINT,
        VIEWER_INSPIRED,
        CLEVERFERRET
    }

    private val familyToThemes: Map<ThemeFamily, List<ThemePack>> = mapOf(
        ThemeFamily.LINKPOINT to listOf(BuiltInThemes.LINKPOINT_DEFAULT),
        ThemeFamily.VIEWER_INSPIRED to listOf(
            BuiltInThemes.SL_CLASSIC,
            BuiltInThemes.FIRESTORM
        ),
        ThemeFamily.CLEVERFERRET to listOf(
            BuiltInThemes.CLEVERFERRET_GOLD,
            BuiltInThemes.NAVY_GOLD,
            BuiltInThemes.ROYAL_SILVER,
            BuiltInThemes.FOREST_COPPER,
            BuiltInThemes.BURGUNDY_ROSEGOLD,
            BuiltInThemes.CHARCOAL_CHAMPAGNE,
            BuiltInThemes.SLATE_GUNMETAL
        )
    )

    fun allThemes(): List<ThemePack> = familyToThemes.values.flatten()

    fun families(): Set<ThemeFamily> = familyToThemes.keys

    fun themesInFamily(family: ThemeFamily): List<ThemePack> = familyToThemes[family].orEmpty()

    fun familyForTheme(themeId: String): ThemeFamily? =
        familyToThemes.entries.firstOrNull { (_, themes) -> themes.any { it.id == themeId } }?.key

    fun getById(id: String): ThemePack? = allThemes().firstOrNull { it.id == id }

    fun familyFor(theme: ThemePack): Family {
        if (!theme.isBuiltIn) return Family.COMMUNITY
        return when (familyForTheme(theme.id)) {
            ThemeFamily.LINKPOINT -> Family.LINKPOINT
            ThemeFamily.VIEWER_INSPIRED -> Family.VIEWER_INSPIRED
            ThemeFamily.CLEVERFERRET -> Family.METALLIC_DARK
            null -> Family.METALLIC_DARK
        }
    }
}
