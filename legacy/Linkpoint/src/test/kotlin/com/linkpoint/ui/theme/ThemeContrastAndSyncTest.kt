package com.linkpoint.ui.theme

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ThemeContrastAndSyncTest {

    private data class ThemeJson(
        val fileRelativePath: String,
        val id: String,
        val name: String,
        val primary: String,
        val onPrimary: String,
        val surface: String,
        val onSurface: String,
        val background: String,
        val onBackground: String
    )

    @Test
    fun testBuiltInThemesWCAGCompliance() {
        val builtInThemes = BuiltInThemes.getAllBuiltInThemes()
        assertTrue("BuiltInThemes catalog must not be empty", builtInThemes.isNotEmpty())

        val failures = mutableListOf<String>()
        for (theme in builtInThemes) {
            val surfRatio = ThemeContrastAudit.contrastRatio(theme.colorOnSurface, theme.colorSurface)
            if (surfRatio < ThemeContrastAudit.MIN_TEXT_CONTRAST) {
                failures.add("${theme.id} ($theme.name): onSurface/surface contrast $surfRatio < ${ThemeContrastAudit.MIN_TEXT_CONTRAST}")
            }

            val priRatio = ThemeContrastAudit.contrastRatio(theme.colorOnPrimary, theme.colorPrimary)
            if (priRatio < ThemeContrastAudit.MIN_LARGE_TEXT_CONTRAST) {
                failures.add("${theme.id} ($theme.name): onPrimary/primary contrast $priRatio < ${ThemeContrastAudit.MIN_LARGE_TEXT_CONTRAST}")
            }
        }

        assertTrue(
            "BuiltInThemes WCAG compliance failures:\n${failures.joinToString("\n")}",
            failures.isEmpty()
        )
    }

    @Test
    fun testThemeJsonWCAGCompliance() {
        val themeJsons = loadAllThemeJsons()
        assertTrue("JSON themes must be found", themeJsons.isNotEmpty())

        val failures = mutableListOf<String>()
        for (theme in themeJsons) {
            val surfRatio = ThemeContrastAudit.contrastRatio(theme.onSurface, theme.surface)
            if (surfRatio < ThemeContrastAudit.MIN_TEXT_CONTRAST) {
                failures.add("${theme.id} (${theme.fileRelativePath}): onSurface/surface contrast $surfRatio < ${ThemeContrastAudit.MIN_TEXT_CONTRAST}")
            }

            val priRatio = ThemeContrastAudit.contrastRatio(theme.onPrimary, theme.primary)
            if (priRatio < ThemeContrastAudit.MIN_LARGE_TEXT_CONTRAST) {
                failures.add("${theme.id} (${theme.fileRelativePath}): onPrimary/primary contrast $priRatio < ${ThemeContrastAudit.MIN_LARGE_TEXT_CONTRAST}")
            }
        }

        assertTrue(
            "JSON themes WCAG compliance failures:\n${failures.joinToString("\n")}",
            failures.isEmpty()
        )
    }

    @Test
    fun testThemeCatalogSynchronization() {
        val themeJsons = loadAllThemeJsons()
        assertTrue("JSON themes must be found", themeJsons.isNotEmpty())

        val missingDefinitions = mutableListOf<String>()
        for (theme in themeJsons) {
            val builtIn = BuiltInThemes.getById(theme.id)
            if (builtIn == null) {
                missingDefinitions.add("JSON theme '${theme.id}' in ${theme.fileRelativePath} has no matching definition in BuiltInThemes")
            }
        }

        assertTrue(
            "Catalog synchronization failures:\n${missingDefinitions.joinToString("\n")}",
            missingDefinitions.isEmpty()
        )
    }

    @Test
    fun testThemeTokenCompleteness() {
        // Test BuiltInThemes completeness
        for (theme in BuiltInThemes.getAllBuiltInThemes()) {
            assertTrue("Theme ID cannot be blank", theme.id.isNotBlank())
            assertTrue("Theme name cannot be blank for ${theme.id}", theme.name.isNotBlank())
            assertValidHexColor("colorPrimary in ${theme.id}", theme.colorPrimary)
            assertValidHexColor("colorOnPrimary in ${theme.id}", theme.colorOnPrimary)
            assertValidHexColor("colorBackground in ${theme.id}", theme.colorBackground)
            assertValidHexColor("colorSurface in ${theme.id}", theme.colorSurface)
            assertValidHexColor("colorOnSurface in ${theme.id}", theme.colorOnSurface)
        }

        // Test JSON themes completeness
        val themeJsons = loadAllThemeJsons()
        for (theme in themeJsons) {
            assertTrue("JSON theme ID cannot be blank in ${theme.fileRelativePath}", theme.id.isNotBlank())
            assertTrue("JSON theme name cannot be blank in ${theme.fileRelativePath}", theme.name.isNotBlank())
            assertValidHexColor("primary in ${theme.fileRelativePath}", theme.primary)
            assertValidHexColor("onPrimary in ${theme.fileRelativePath}", theme.onPrimary)
            assertValidHexColor("surface in ${theme.fileRelativePath}", theme.surface)
            assertValidHexColor("onSurface in ${theme.fileRelativePath}", theme.onSurface)
            assertValidHexColor("background in ${theme.fileRelativePath}", theme.background)
        }
    }

    private fun assertValidHexColor(fieldDesc: String, hexColor: String) {
        assertTrue("$fieldDesc must not be blank", hexColor.isNotBlank())
        try {
            ThemeContrastAudit.calculateLuminance(hexColor)
        } catch (e: Exception) {
            throw AssertionError("$fieldDesc has invalid hex color format: '$hexColor'", e)
        }
    }

    private fun loadAllThemeJsons(): List<ThemeJson> {
        val root = findRepoRoot()
        val candidatePaths = listOf(
            root.resolve("packages/design-system/themes"),
            root.resolve("packages/design-system/themes/community"),
            root.resolve("ktheme-pr/themes/community"),
            root.resolve("legacy/ktheme-pr/themes/community")
        )

        val jsonFiles = mutableSetOf<Path>()

        for (dir in candidatePaths) {
            if (Files.isDirectory(dir)) {
                Files.list(dir).use { stream ->
                    stream.filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }.forEach { jsonFiles.add(it) }
                }
            }
        }

        return jsonFiles.map { path ->
            val content = String(Files.readAllBytes(path), StandardCharsets.UTF_8)
            val json = JSONObject(content)

            val metadata = json.optJSONObject("metadata")
            val id = metadata?.optString("id") ?: json.optString("id")
            val name = metadata?.optString("name") ?: json.optString("name")

            val cs = json.optJSONObject("colorScheme") ?: json.optJSONObject("colors")
            val primary = cs?.optString("primary").orEmpty()
            val onPrimary = cs?.optString("onPrimary").orEmpty()
            val surface = cs?.optString("surface").orEmpty()
            val onSurface = cs?.optString("onSurface").orEmpty()
            val background = cs?.optString("background") ?: surface
            val onBackground = cs?.optString("onBackground") ?: onSurface

            ThemeJson(
                fileRelativePath = root.relativize(path).toString(),
                id = id,
                name = name,
                primary = primary,
                onPrimary = onPrimary,
                surface = surface,
                onSurface = onSurface,
                background = background,
                onBackground = onBackground
            )
        }
    }

    private fun findRepoRoot(): Path {
        var current = Paths.get("").toAbsolutePath().normalize()
        repeat(6) {
            if (Files.exists(current.resolve(".git"))) return current
            current = current.parent ?: return@repeat
        }
        error("Could not locate repository root from ${Paths.get("").toAbsolutePath()}")
    }
}
