package com.linkpoint.ui.theme

import android.content.Context
import com.ktheme.models.Theme
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Catalog of built-in themes loaded dynamically from application assets using com.ktheme.models.Theme
 * as the canonical domain model.
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

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Volatile
    private var cachedKthemes: List<Theme>? = null

    @Volatile
    private var cachedThemePacks: List<ThemePack>? = null

    /**
     * Dynamically loads all canonical Theme objects from application assets or file sources.
     */
    @Synchronized
    fun allKthemes(context: Context? = null): List<Theme> {
        cachedKthemes?.let { return it }

        val themeMap = linkedMapOf<String, Theme>()

        // 1. Try loading from official com.ktheme.library.KthemeAPI
        try {
            val apiThemes = com.ktheme.library.KthemeAPI.getAvailableThemes()
            for (theme in apiThemes) {
                if (theme.metadata.id.isNotBlank()) {
                    themeMap[theme.metadata.id] = theme
                }
            }
        } catch (e: Exception) {
            // Fallback
        }

        // 2. Try loading from Android AssetManager if context is provided
        if (themeMap.isEmpty() && context != null) {
            try {
                val assetFiles = context.assets.list("themes").orEmpty()
                for (fileName in assetFiles.sorted()) {
                    if (fileName.endsWith(".json", ignoreCase = true)) {
                        try {
                            val jsonString = context.assets.open("themes/$fileName").bufferedReader().use { it.readText() }
                            val theme = json.decodeFromString<Theme>(jsonString)
                            if (theme.metadata.id.isNotBlank()) {
                                themeMap[theme.metadata.id] = theme
                            }
                        } catch (e: Exception) {
                            // Skip malformed files
                        }
                    }
                }
            } catch (e: Exception) {
                // Asset reading exception fallback
            }
        }

        // 2. Fallback to file paths if asset loading didn't find themes (e.g. JVM tests)
        if (themeMap.isEmpty()) {
            val possibleDirs = listOf(
                File("src/main/assets/themes"),
                File("Linkpoint/src/main/assets/themes"),
                File("legacy/Linkpoint/src/main/assets/themes"),
                File("packages/design-system/themes"),
                File("Linkpoint/packages/design-system/themes"),
                File("../packages/design-system/themes"),
                File("../../packages/design-system/themes"),
                File("ktheme-pr/themes/community"),
                File("Linkpoint/ktheme-pr/themes/community"),
                File("legacy/ktheme-pr/themes/community"),
                File("../ktheme-pr/themes/community"),
                File("../../legacy/ktheme-pr/themes/community")
            )

            for (dir in possibleDirs) {
                if (dir.exists() && dir.isDirectory) {
                    dir.listFiles { f -> f.isFile && f.name.endsWith(".json") }?.sortedBy { it.name }?.forEach { file ->
                        try {
                            val theme = json.decodeFromString<Theme>(file.readText())
                            if (theme.metadata.id.isNotBlank() && !themeMap.containsKey(theme.metadata.id)) {
                                themeMap[theme.metadata.id] = theme
                            }
                        } catch (e: Exception) {
                            // Ignore malformed test files
                        }
                    }
                }
            }
        }

        val loaded = themeMap.values.toList()
        if (loaded.isNotEmpty()) {
            cachedKthemes = loaded
            cachedThemePacks = loaded.map { it.toThemePack(isBuiltIn = true) }
        }

        return loaded
    }

    /**
     * Dynamically loads all ThemePacks converted from canonical Theme objects.
     */
    fun allThemes(context: Context? = null): List<ThemePack> {
        allKthemes(context)
        return cachedThemePacks ?: emptyList()
    }

    fun families(): Set<ThemeFamily> = ThemeFamily.entries.toSet()

    fun themesInFamily(family: ThemeFamily, context: Context? = null): List<ThemePack> {
        return allThemes(context).filter { familyForTheme(it.id) == family }
    }

    fun familyForTheme(themeId: String): ThemeFamily? {
        val themePack = getById(themeId) ?: return null
        val tags = themePack.ktheme?.metadata?.tags.orEmpty().map { it.lowercase() }

        return when (themeId) {
            "linkpoint_default" -> ThemeFamily.LINKPOINT
            "ink-terminal-modern", "neo-noir-neon", "metro-cyan", "aurora-glass-night", "slate-cyan" -> ThemeFamily.TERMINAL_NEON
            "lcars", "lcars-tng", "midnight-amber", "royal-bronze", "forest-copper", "obsidian-crimson" -> ThemeFamily.CONSOLE_AMBER
            "navy-gold", "art-deco", "emerald-silver", "royal-silver", "deep-purple-platinum", "charcoal-champagne", "slate-gunmetal", "rose-gold", "burgundy-rose-gold", "cleverferret_gold" -> ThemeFamily.METAL_JEWEL
            "frutiger-aero", "paper-ink", "art-nouveau", "calm-clinical", "solarpunk-civic" -> ThemeFamily.DAYLIGHT
            "sl_classic", "firestorm" -> ThemeFamily.VIEWER_INSPIRED
            "windows-phone-metro", "stargate-atlantis", "stargate-sg1" -> ThemeFamily.COMMUNITY
            else -> {
                when {
                    tags.any { it in listOf("light", "daylight", "clinical", "paper", "reader") } -> ThemeFamily.DAYLIGHT
                    tags.any { it in listOf("terminal", "neon", "cyan", "glass", "modern") } -> ThemeFamily.TERMINAL_NEON
                    tags.any { it in listOf("amber", "lcars", "console", "crimson", "copper") } -> ThemeFamily.CONSOLE_AMBER
                    tags.any { it in listOf("metallic", "gold", "silver", "deco", "luxury", "jewel") } -> ThemeFamily.METAL_JEWEL
                    tags.any { it in listOf("viewer", "classic", "firestorm") } -> ThemeFamily.VIEWER_INSPIRED
                    else -> ThemeFamily.COMMUNITY
                }
            }
        }
    }

    fun getById(id: String, context: Context? = null): ThemePack? {
        return allThemes(context).firstOrNull { it.id == id }
    }

    fun getKthemeById(id: String, context: Context? = null): Theme? {
        return allKthemes(context).firstOrNull { it.metadata.id == id }
    }

    @Synchronized
    fun resetCache() {
        cachedKthemes = null
        cachedThemePacks = null
    }
}
