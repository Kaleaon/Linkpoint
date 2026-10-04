package com.linkpoint.ui.theme

import com.ktheme.models.LayoutStructure

/**
 * Built-in theme packs from Ktheme / linkpoint-design.
 * Supports all 24 core Ktheme color palettes plus community and viewer-inspired skins.
 */
object BuiltInThemes {

    /** Default Linkpoint theme - Blue accent on dark background */
    val LINKPOINT_DEFAULT = ThemePack(
        id = "linkpoint_default",
        name = "Linkpoint Blue",
        description = "The default Linkpoint theme with blue accents",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#1976D2",
        colorPrimaryDark = "#0D47A1",
        colorOnPrimary = "#FFFFFF",
        colorSecondary = "#00BCD4",
        colorOnSecondary = "#000000",
        colorBackground = "#1A1A1A",
        colorSurface = "#2D2D2D",
        colorOnSurface = "#FFFFFF",
        colorOnSurfaceVariant = "#B0B0B0",
        densityProfile = DensityProfile.STANDARD,
        cornerProfile = CornerProfile.ROUNDED,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Ink Terminal Modern — Phosphor green on near-black */
    val INK_TERMINAL = ThemePack(
        id = "ink-terminal-modern",
        name = "Ink Terminal",
        description = "Monospaced phosphor green on near-black terminal grid",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#6CFF9A",
        colorPrimaryDark = "#1F6640",
        colorOnPrimary = "#0A1112",
        colorSecondary = "#3E4E5E",
        colorOnSecondary = "#D7F5E6",
        colorBackground = "#0A1112",
        colorSurface = "#101A1C",
        colorOnSurface = "#D7F5E6",
        colorOnSurfaceVariant = "#A7C8BC",
        colorSurfaceVariant = "#1B2A2D",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.TERMINAL
    )

    /** LCARS TNG — Authentic Star Trek LCARS amber/lilac console */
    val LCARS_TNG = ThemePack(
        id = "lcars-tng",
        name = "LCARS Amber",
        description = "Warm amber/lilac LCARS palette tuned for LCARS consoles",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#F2A65A",
        colorPrimaryDark = "#CC7A2B",
        colorOnPrimary = "#120C1C",
        colorSecondary = "#A485F7",
        colorOnSecondary = "#120C1C",
        colorBackground = "#120C1C",
        colorSurface = "#1C132A",
        colorOnSurface = "#F3E9FF",
        colorOnSurfaceVariant = "#D0B3E6",
        colorSurfaceVariant = "#3D1F5C",
        colorError = "#CF6679",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.PILLED,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.LCARS
    )

    /** Metro Cyan — Flat cyan on deep navy blue */
    val METRO_CYAN = ThemePack(
        id = "metro-cyan",
        name = "Metro Cyan",
        description = "High-contrast Windows Phone Metro layout with flat cyan accent",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#00AEEF",
        colorPrimaryDark = "#0070CA",
        colorOnPrimary = "#001A33",
        colorSecondary = "#2D89EF",
        colorOnSecondary = "#001A33",
        colorBackground = "#001A33",
        colorSurface = "#002448",
        colorOnSurface = "#F0F8FF",
        colorOnSurfaceVariant = "#B8CAD6",
        colorSurfaceVariant = "#3D4854",
        colorError = "#E81123",
        densityProfile = DensityProfile.COMFORTABLE,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.METRO
    )

    /** Frutiger Aero — Sky and nature light theme */
    val FRUTIGER_AERO = ThemePack(
        id = "frutiger-aero",
        name = "Frutiger Aero",
        description = "Sky blue and glossy nature green with curved translucent glass",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#39B6F0",
        colorPrimaryDark = "#A9E6FF",
        colorOnPrimary = "#173A52",
        colorSecondary = "#79D87E",
        colorOnSecondary = "#173A52",
        colorBackground = "#EAF7FF",
        colorSurface = "#F7FCFF",
        colorOnSurface = "#173A52",
        colorOnSurfaceVariant = "#34566E",
        colorSurfaceVariant = "#DDF1FF",
        colorError = "#BA1A1A",
        densityProfile = DensityProfile.COMFORTABLE,
        cornerProfile = CornerProfile.ROUNDED,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.FRUTIGER_AERO
    )

    /** Navy + Gold — Metallic gold on deep navy */
    val NAVY_GOLD = ThemePack(
        id = "navy-gold",
        name = "Navy Gold",
        description = "Elegant and professional - deep navy with rich gold accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#D4AF37",
        colorPrimaryDark = "#715F33",
        colorOnPrimary = "#0A1630",
        colorSecondary = "#4A90E2",
        colorOnSecondary = "#0A1630",
        colorBackground = "#0A1630",
        colorSurface = "#1A2645",
        colorOnSurface = "#E8E3D8",
        colorOnSurfaceVariant = "#C9C4B9",
        colorSurfaceVariant = "#2A3655",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Paper & Ink — Hueless light theme */
    val PAPER_INK = ThemePack(
        id = "paper-ink",
        name = "Paper & Ink",
        description = "Hueless high-contrast monochrome paper skin for sunlight legibility",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#2C2C2C",
        colorPrimaryDark = "#121212",
        colorOnPrimary = "#F0F0EB",
        colorSecondary = "#595959",
        colorOnSecondary = "#F0F0EB",
        colorBackground = "#F0F0EB",
        colorSurface = "#FAF9F6",
        colorOnSurface = "#2C2C2C",
        colorOnSurfaceVariant = "#454545",
        colorSurfaceVariant = "#EBEAE4",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Art Deco — Hairline gold on ivory-black */
    val ART_DECO = ThemePack(
        id = "art-deco",
        name = "Art Deco",
        description = "Architectural gold hairlines on deep ivory-black",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#D4AF37",
        colorPrimaryDark = "#977B2F",
        colorOnPrimary = "#0B0A0A",
        colorSecondary = "#F4E7CF",
        colorOnSecondary = "#0B0A0A",
        colorBackground = "#0B0A0A",
        colorSurface = "#141314",
        colorOnSurface = "#F3E8D0",
        colorOnSurfaceVariant = "#C9BDA2",
        colorSurfaceVariant = "#232124",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.ART_DECO
    )

    /** Neo-Noir Neon — Electric purple and cyan glow */
    val NEO_NOIR_NEON = ThemePack(
        id = "neo-noir-neon",
        name = "Neo-Noir Neon",
        description = "Dystopian cyberpunk violet and cyan neon glow on dark metallic chrome",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#AA43FF",
        colorPrimaryDark = "#4B1D73",
        colorOnPrimary = "#090A10",
        colorSecondary = "#00D1FF",
        colorOnSecondary = "#090A10",
        colorBackground = "#090A10",
        colorSurface = "#111420",
        colorOnSurface = "#E7EAF7",
        colorOnSurfaceVariant = "#B9C0D8",
        colorSurfaceVariant = "#1C2130",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.CYBERPUNK
    )

    /** Emerald Silver — Silver on deep emerald green */
    val EMERALD_SILVER = ThemePack(
        id = "emerald-silver",
        name = "Emerald Silver",
        description = "Metallic silver accents on deep emerald green",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#C0C0C0",
        colorPrimaryDark = "#505050",
        colorOnPrimary = "#0D3B2E",
        colorSecondary = "#50C878",
        colorOnSecondary = "#0D3B2E",
        colorBackground = "#0D3B2E",
        colorSurface = "#1A5544",
        colorOnSurface = "#E8F5E8",
        colorOnSurfaceVariant = "#C9E4D9",
        colorSurfaceVariant = "#2A6554",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Midnight Amber — Amber on midnight blue */
    val MIDNIGHT_AMBER = ThemePack(
        id = "midnight-amber",
        name = "Midnight Amber",
        description = "Golden amber accents on midnight blue chrome",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#FFBF00",
        colorPrimaryDark = "#CC9900",
        colorOnPrimary = "#0C1824",
        colorSecondary = "#D4A76A",
        colorOnSecondary = "#0C1824",
        colorBackground = "#0C1824",
        colorSurface = "#15202E",
        colorOnSurface = "#E8EEF5",
        colorOnSurfaceVariant = "#B8C5D6",
        colorSurfaceVariant = "#253447",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Obsidian Crimson — Crimson on obsidian black */
    val OBSIDIAN_CRIMSON = ThemePack(
        id = "obsidian-crimson",
        name = "Obsidian Crimson",
        description = "Vivid crimson red on deep obsidian black",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#DC143C",
        colorPrimaryDark = "#B00F30",
        colorOnPrimary = "#F5F5F5",
        colorSecondary = "#A8505A",
        colorOnSecondary = "#F5F5F5",
        colorBackground = "#0A0A0A",
        colorSurface = "#141414",
        colorOnSurface = "#F5F5F5",
        colorOnSurfaceVariant = "#D0D0D0",
        colorSurfaceVariant = "#2D2D2D",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.CYBERPUNK
    )

    /** Art Nouveau — Organic botanical curves */
    val ART_NOUVEAU = ThemePack(
        id = "art-nouveau",
        name = "Art Nouveau",
        description = "Organic curves, botanical accents, and warm earth tones",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#7B5737",
        colorPrimaryDark = "#D9C2A9",
        colorOnPrimary = "#F6F0E6",
        colorSecondary = "#769762",
        colorOnSecondary = "#2C2218",
        colorBackground = "#F6F0E6",
        colorSurface = "#FFF8EE",
        colorOnSurface = "#2C2218",
        colorOnSurfaceVariant = "#584638",
        colorSurfaceVariant = "#E7D8C7",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Aurora Glass Night — Frosted night glass with aurora accents */
    val AURORA_GLASS_NIGHT = ThemePack(
        id = "aurora-glass-night",
        name = "Aurora Glass Night",
        description = "Frosted glass aesthetic with glowing aurora cyan and violet",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#6DE8FF",
        colorPrimaryDark = "#2A6F85",
        colorOnPrimary = "#0A1224",
        colorSecondary = "#8C7CFF",
        colorOnSecondary = "#0A1224",
        colorBackground = "#0A1224",
        colorSurface = "#101C33",
        colorOnSurface = "#E7F0FF",
        colorOnSurfaceVariant = "#B5C7E9",
        colorSurfaceVariant = "#1D2B4A",
        densityProfile = DensityProfile.STANDARD,
        cornerProfile = CornerProfile.ROUNDED,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.MODERN_GLASS
    )

    /** Burgundy Rose Gold — Deep burgundy with rose gold */
    val BURGUNDY_ROSEGOLD = ThemePack(
        id = "burgundy-rose-gold",
        name = "Burgundy Rose Gold",
        description = "Rich burgundy with elegant rose gold metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#B76E79",
        colorPrimaryDark = "#93575F",
        colorOnPrimary = "#2D0F1A",
        colorSecondary = "#4D1A2A",
        colorOnSecondary = "#FFE6ED",
        colorBackground = "#2D0F1A",
        colorSurface = "#3D1525",
        colorOnSurface = "#FFE6ED",
        colorOnSurfaceVariant = "#E6C0CC",
        colorSurfaceVariant = "#5C2A3D",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Calm Clinical — Low-stress healthcare cyan/green */
    val CALM_CLINICAL = ThemePack(
        id = "calm-clinical",
        name = "Calm Clinical",
        description = "Low-stress admin palette with clear status readability",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#38779E",
        colorPrimaryDark = "#C4DFF2",
        colorOnPrimary = "#F5FAFD",
        colorSecondary = "#61B48B",
        colorOnSecondary = "#1F394B",
        colorBackground = "#F5FAFD",
        colorSurface = "#FFFFFF",
        colorOnSurface = "#1F394B",
        colorOnSurfaceVariant = "#445F72",
        colorSurfaceVariant = "#E5EEF4",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Charcoal Champagne — Charcoal with champagne gold */
    val CHARCOAL_CHAMPAGNE = ThemePack(
        id = "charcoal-champagne",
        name = "Charcoal Champagne",
        description = "Sophisticated charcoal gray with warm champagne gold",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#F7E7CE",
        colorPrimaryDark = "#C5B8A5",
        colorOnPrimary = "#1F1F1F",
        colorSecondary = "#3D3D3D",
        colorOnSecondary = "#F5F5F5",
        colorBackground = "#1F1F1F",
        colorSurface = "#2A2A2A",
        colorOnSurface = "#F5F5F5",
        colorOnSurfaceVariant = "#D0D0D0",
        colorSurfaceVariant = "#3D3D3D",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Deep Purple Platinum — Platinum on deep purple */
    val DEEP_PURPLE_PLATINUM = ThemePack(
        id = "deep-purple-platinum",
        name = "Deep Purple Platinum",
        description = "Deep purple background with luxurious platinum metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#E5E4E2",
        colorPrimaryDark = "#B8B7B5",
        colorOnPrimary = "#1A0F2E",
        colorSecondary = "#2E1A50",
        colorOnSecondary = "#F0EBFF",
        colorBackground = "#1A0F2E",
        colorSurface = "#24153D",
        colorOnSurface = "#F0EBFF",
        colorOnSurfaceVariant = "#D0C0E6",
        colorSurfaceVariant = "#3D2A5C",
        densityProfile = DensityProfile.STANDARD,
        cornerProfile = CornerProfile.ROUNDED,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.MODERN_GLASS
    )

    /** Forest Copper — Copper on forest green */
    val FOREST_COPPER = ThemePack(
        id = "forest-copper",
        name = "Forest Copper",
        description = "Deep forest green with warm copper metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#B87333",
        colorPrimaryDark = "#935E29",
        colorOnPrimary = "#0D1F0D",
        colorSecondary = "#1A3D1A",
        colorOnSecondary = "#E8F5E8",
        colorBackground = "#0D1F0D",
        colorSurface = "#152915",
        colorOnSurface = "#E8F5E8",
        colorOnSurfaceVariant = "#B8D9B8",
        colorSurfaceVariant = "#2A4D2A",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Rose Gold — Warm rose gold with burgundy */
    val ROSE_GOLD = ThemePack(
        id = "rose-gold",
        name = "Rose Gold",
        description = "Warm and elegant rose gold with burgundy undertones",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#C1818B",
        colorPrimaryDark = "#7D4A52",
        colorOnPrimary = "#3D1F2B",
        colorSecondary = "#D4A5A5",
        colorOnSecondary = "#3D1F2B",
        colorBackground = "#3D1F2B",
        colorSurface = "#4D2F3B",
        colorOnSurface = "#F5E5E8",
        colorOnSurfaceVariant = "#E5D5D8",
        colorSurfaceVariant = "#5D3F4B",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Royal Bronze — Bronze on royal purple */
    val ROYAL_BRONZE = ThemePack(
        id = "royal-bronze",
        name = "Royal Bronze",
        description = "Regal deep purple with bronze metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#CD7F32",
        colorPrimaryDark = "#975929",
        colorOnPrimary = "#1A0A30",
        colorSecondary = "#2D1550",
        colorOnSecondary = "#F0E6FF",
        colorBackground = "#1A0A30",
        colorSurface = "#220D40",
        colorOnSurface = "#F0E6FF",
        colorOnSurfaceVariant = "#D0B3E6",
        colorSurfaceVariant = "#3D1F5C",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Royal Silver — Silver on royal purple */
    val ROYAL_SILVER = ThemePack(
        id = "royal-silver",
        name = "Royal Silver",
        description = "Royal purple background with silver metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#C0C0C0",
        colorPrimaryDark = "#9A9A9A",
        colorOnPrimary = "#1A1535",
        colorSecondary = "#2A1F50",
        colorOnSecondary = "#F0EBFF",
        colorBackground = "#1A1535",
        colorSurface = "#211A40",
        colorOnSurface = "#F0EBFF",
        colorOnSurfaceVariant = "#C8BFE6",
        colorSurfaceVariant = "#3D2F5C",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Slate Cyan — Cyan on slate gray */
    val SLATE_CYAN = ThemePack(
        id = "slate-cyan",
        name = "Slate Cyan",
        description = "Modern slate gray with vibrant cyan accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#00D9FF",
        colorPrimaryDark = "#00A8CC",
        colorOnPrimary = "#1A1F24",
        colorSecondary = "#2A333D",
        colorOnSecondary = "#E8F0F5",
        colorBackground = "#1A1F24",
        colorSurface = "#232930",
        colorOnSurface = "#E8F0F5",
        colorOnSurfaceVariant = "#B8CAD6",
        colorSurfaceVariant = "#3D4854",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Slate Gunmetal — Industrial gunmetal on slate */
    val SLATE_GUNMETAL = ThemePack(
        id = "slate-gunmetal",
        name = "Slate Gunmetal",
        description = "Industrial slate gray with gunmetal metallic accents",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#8F9CA8",
        colorPrimaryDark = "#7D8A94",
        colorOnPrimary = "#1A2029",
        colorSecondary = "#2D3844",
        colorOnSecondary = "#E6ECF2",
        colorBackground = "#1A2029",
        colorSurface = "#232C38",
        colorOnSurface = "#E6ECF2",
        colorOnSurfaceVariant = "#B8C5D6",
        colorSurfaceVariant = "#3D4854",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Solarpunk Civic — Daylight civic green */
    val SOLARPUNK_CIVIC = ThemePack(
        id = "solarpunk-civic",
        name = "Solarpunk Civic",
        description = "Optimistic daylight green civic palette with clear trust-building status",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#38B56A",
        colorPrimaryDark = "#BFEFD0",
        colorOnPrimary = "#1E3A27",
        colorSecondary = "#4FAEEA",
        colorOnSecondary = "#1E3A27",
        colorBackground = "#F2FBF4",
        colorSurface = "#FBFFFC",
        colorOnSurface = "#1E3A27",
        colorOnSurfaceVariant = "#456355",
        colorSurfaceVariant = "#E2F2E8",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** CleverFerret Gold */
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
        colorOnSurfaceVariant = "#CAC4D0",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** Second Life official viewer look */
    val SL_CLASSIC = ThemePack(
        id = "sl_classic",
        name = "Second Life Viewer",
        description = "Classic Linden Lab viewer look - Linden blue on deep slate",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#4AA3DF",
        colorPrimaryDark = "#0066CC",
        colorOnPrimary = "#0B1520",
        colorSecondary = "#7FB3D5",
        colorOnSecondary = "#0B1520",
        colorBackground = "#1B2430",
        colorSurface = "#263548",
        colorOnSurface = "#E6ECF2",
        colorOnSurfaceVariant = "#AEBECF",
        layoutStructure = LayoutStructure.MATERIAL3
    )

    /** LCARS Core Theme */
    val LCARS = ThemePack(
        id = "lcars",
        name = "LCARS",
        description = "LCARS-inspired interface with warm rails and compact controls",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#F2A65A",
        colorPrimaryDark = "#CC7A2B",
        colorOnPrimary = "#1B0E24",
        colorSecondary = "#C5678D",
        colorOnSecondary = "#2B1224",
        colorBackground = "#120C1C",
        colorSurface = "#1C132A",
        colorOnSurface = "#F3E9FF",
        colorOnSurfaceVariant = "#D0B3E6",
        colorSurfaceVariant = "#3D1F5C",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.PILLED,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.LCARS
    )

    /** Windows Phone Metro Core Theme */
    val WINDOWS_PHONE_METRO = ThemePack(
        id = "windows-phone-metro",
        name = "Windows Phone Metro",
        description = "Flat, tile-first Metro-inspired interface theme",
        author = "Ktheme",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#00AEEF",
        colorPrimaryDark = "#0078D7",
        colorOnPrimary = "#00151F",
        colorSecondary = "#005A9E",
        colorOnSecondary = "#EAF4FF",
        colorBackground = "#001A33",
        colorSurface = "#002448",
        colorOnSurface = "#F0F8FF",
        colorOnSurfaceVariant = "#B8CAD6",
        colorSurfaceVariant = "#3D4854",
        densityProfile = DensityProfile.COMFORTABLE,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.METRO
    )

    /** Firestorm "Starlight-Dark" look */
    val FIRESTORM = ThemePack(
        id = "firestorm",
        name = "Firestorm Viewer",
        description = "Firestorm third-party viewer look - fire orange on near-black",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#E85D00",
        colorPrimaryDark = "#B84800",
        colorOnPrimary = "#1A0E00",
        colorSecondary = "#FFB366",
        colorOnSecondary = "#1A0E00",
        colorBackground = "#121212",
        colorSurface = "#1E1E1E",
        colorOnSurface = "#F5F5F5",
        colorOnSurfaceVariant = "#BDBDBD",
        densityProfile = DensityProfile.COMPACT,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.CYBERPUNK
    )

    /** Stargate Atlantis */
    val STARGATE_ATLANTIS = ThemePack(
        id = "stargate-atlantis",
        name = "Stargate Atlantis",
        description = "Atlantean blue glass and bronze — Art Deco geometry, prairie banding",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#C4A062",
        colorPrimaryDark = "#8A6E3D",
        colorOnPrimary = "#0B1B2E",
        colorSecondary = "#6FCFE8",
        colorOnSecondary = "#001E2B",
        colorBackground = "#0B1B2E",
        colorSurface = "#133355",
        colorOnSurface = "#E8EFF7",
        colorOnSurfaceVariant = "#8FB0CC",
        colorSurfaceVariant = "#1A4068",
        colorError = "#C46462",
        densityProfile = DensityProfile.COMFORTABLE,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.STANDARD,
        layoutStructure = LayoutStructure.ART_DECO
    )

    /** Stargate SG-1 */
    val STARGATE_SG1 = ThemePack(
        id = "stargate-sg1",
        name = "Stargate SG-1",
        description = "Iris-bronze and event-horizon teal — gate-room console feel",
        author = "Linkpoint",
        version = "1.0.0",
        isBuiltIn = true,
        colorPrimary = "#FFC15A",
        colorPrimaryDark = "#B07820",
        colorOnPrimary = "#1A0E04",
        colorSecondary = "#4FE0FF",
        colorOnSecondary = "#001E2B",
        colorBackground = "#04141C",
        colorSurface = "#0E2C3E",
        colorOnSurface = "#E8F4FF",
        colorOnSurfaceVariant = "#94B5C9",
        colorSurfaceVariant = "#143A52",
        colorError = "#FF6B6B",
        densityProfile = DensityProfile.STANDARD,
        cornerProfile = CornerProfile.SHARP,
        motionProfile = MotionProfile.EXPRESSIVE,
        layoutStructure = LayoutStructure.CYBERPUNK
    )

    /** Get all built-in themes as a list */
    fun getAllBuiltInThemes(): List<ThemePack> = ThemeCatalog.allThemes()

    /** Get a built-in theme by ID */
    fun getById(id: String): ThemePack? = ThemeCatalog.getById(id)
}
