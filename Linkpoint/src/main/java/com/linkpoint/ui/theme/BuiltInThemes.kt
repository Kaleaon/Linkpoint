package com.linkpoint.ui.theme

/**
 * Built-in theme packs from CleverFerret.
 * These themes feature metallic accent colors on elegant dark backgrounds.
 */
object BuiltInThemes {
    
    /**
     * Default Linkpoint theme - Blue accent on dark background
     */
    val LINKPOINT_DEFAULT = ThemePack(
        id = "linkpoint_default",
        name = "Linkpoint Blue",
        description = "The default Linkpoint theme with blue accents",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#4A90D9",
        colorPrimaryDark = "#2E6AB0",
        colorOnPrimary = "#FFFFFF",
        colorSecondary = "#00BCD4",
        colorOnSecondary = "#000000",
        colorBackground = "#1A1A1A",
        colorSurface = "#2D2D2D",
        colorOnSurface = "#FFFFFF",
        colorOnSurfaceVariant = "#B0B0B0"
    )
    
    /**
     * CleverFerret Gold - Elegant gold accents
     */
    val CLEVERFERRET_GOLD = ThemePack(
        id = "cleverferret_gold",
        name = "CleverFerret Gold",
        description = "Elegant gold accents on dark background - from CleverFerret",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#E5A00D",
        colorPrimaryDark = "#CC8A00",
        colorOnPrimary = "#1E1E1E",
        colorSecondary = "#B8860B",
        colorOnSecondary = "#1E1E1E",
        colorBackground = "#1E1E1E",
        colorSurface = "#2A2A2A",
        colorOnSurface = "#E6E1E5",
        colorOnSurfaceVariant = "#CAC4D0"
    )
    
    /**
     * Navy + Gold - Elegant and Professional
     */
    val NAVY_GOLD = ThemePack(
        id = "navy_gold",
        name = "Navy Gold",
        description = "Elegant and professional - deep navy with rich gold accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#D4AF37",
        colorPrimaryDark = "#B8860B",
        colorOnPrimary = "#0A1630",
        colorSecondary = "#C5A572",
        colorOnSecondary = "#0A1630",
        colorBackground = "#0A1630",
        colorSurface = "#0D1B36",
        colorOnSurface = "#E6EAF2",
        colorOnSurfaceVariant = "#B3BFD6"
    )
    
    /**
     * Royal Purple + Silver - Regal and Modern
     */
    val ROYAL_SILVER = ThemePack(
        id = "royal_silver",
        name = "Royal Silver",
        description = "Regal and modern - royal purple with silver metallic accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#C0C0C0",
        colorPrimaryDark = "#71706E",
        colorOnPrimary = "#1A0F2E",
        colorSecondary = "#6B4BA3",
        colorOnSecondary = "#FFFFFF",
        colorBackground = "#1A0F2E",
        colorSurface = "#2D1B4E",
        colorOnSurface = "#F0ECF5",
        colorOnSurfaceVariant = "#D1C4E0"
    )
    
    /**
     * Forest Green + Copper - Natural and Warm
     */
    val FOREST_COPPER = ThemePack(
        id = "forest_copper",
        name = "Forest Copper",
        description = "Natural and warm - deep forest green with copper metallic accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#B87333",
        colorPrimaryDark = "#964B00",
        colorOnPrimary = "#0D1F12",
        colorSecondary = "#C46210",
        colorOnSecondary = "#0D1F12",
        colorBackground = "#0D1F12",
        colorSurface = "#1A3520",
        colorOnSurface = "#E8F5E9",
        colorOnSurfaceVariant = "#C8E6C9"
    )
    
    /**
     * Burgundy + Rose Gold - Luxurious and Elegant
     */
    val BURGUNDY_ROSEGOLD = ThemePack(
        id = "burgundy_rosegold",
        name = "Burgundy Rose Gold",
        description = "Luxurious and elegant - deep burgundy with rose gold metallic accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#B76E79",
        colorPrimaryDark = "#9B6B6F",
        colorOnPrimary = "#2B0D1A",
        colorSecondary = "#C9ADA7",
        colorOnSecondary = "#2B0D1A",
        colorBackground = "#2B0D1A",
        colorSurface = "#4A1629",
        colorOnSurface = "#FFF0F5",
        colorOnSurfaceVariant = "#FFD6E5"
    )
    
    /**
     * Charcoal + Champagne - Sophisticated and Subtle
     */
    val CHARCOAL_CHAMPAGNE = ThemePack(
        id = "charcoal_champagne",
        name = "Charcoal Champagne",
        description = "Sophisticated and subtle - charcoal with champagne gold accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#F7E7CE",
        colorPrimaryDark = "#D4C5B0",
        colorOnPrimary = "#1C1C1E",
        colorSecondary = "#DDD0B4",
        colorOnSecondary = "#1C1C1E",
        colorBackground = "#1C1C1E",
        colorSurface = "#2C2C2E",
        colorOnSurface = "#F2F2F7",
        colorOnSurfaceVariant = "#E5E5EA"
    )
    
    /**
     * Slate + Gunmetal - Modern and Industrial
     */
    val SLATE_GUNMETAL = ThemePack(
        id = "slate_gunmetal",
        name = "Slate Gunmetal",
        description = "Modern and industrial - slate gray with gunmetal metallic accents",
        author = "CleverFerret",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#6B7F8A",
        colorPrimaryDark = "#3E4E57",
        colorOnPrimary = "#0F1419",
        colorSecondary = "#AAA9AD",
        colorOnSecondary = "#0F1419",
        colorBackground = "#0F1419",
        colorSurface = "#1A232B",
        colorOnSurface = "#ECF0F5",
        colorOnSurfaceVariant = "#CFD8E0"
    )
    
    /**
     * Get all built-in themes as a list
     */
    fun getAllBuiltInThemes(): List<ThemePack> = listOf(
        LINKPOINT_DEFAULT,
        CLEVERFERRET_GOLD,
        NAVY_GOLD,
        ROYAL_SILVER,
        FOREST_COPPER,
        BURGUNDY_ROSEGOLD,
        CHARCOAL_CHAMPAGNE,
        SLATE_GUNMETAL
    )
    
    /**
     * Get a built-in theme by ID
     */
    fun getById(id: String): ThemePack? {
        return getAllBuiltInThemes().find { it.id == id }
    }
}
