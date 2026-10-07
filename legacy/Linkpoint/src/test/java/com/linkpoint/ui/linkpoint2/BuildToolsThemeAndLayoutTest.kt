package com.linkpoint.ui.linkpoint2

import androidx.compose.ui.graphics.Color
import com.ktheme.models.ColorScheme
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2Tokens
import com.linkpoint.ui.components.linkpoint2.tokens.L2Radii
import com.linkpoint.ui.components.linkpoint2.tokens.L2Spacing
import com.linkpoint.ui.components.linkpoint2.tokens.L2TypeScale
import com.linkpoint.ui.components.linkpoint2.tokens.L2Aurora
import com.linkpoint.ui.components.linkpoint2.tokens.L2Glass
import com.linkpoint.ui.theme.BuiltInThemes
import com.linkpoint.ui.theme.toKthemeTheme
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BuildToolsThemeAndLayoutTest {

    @Test
    fun colorScheme_defaultAxisTokens_arePresent() {
        val scheme = ColorScheme(
            primary = "#123456",
            onPrimary = "#FFFFFF"
        )
        assertEquals("#FF6B6B", scheme.axisX)
        assertEquals("#7CFFD8", scheme.axisY)
        assertEquals("#6DE8FF", scheme.axisZ)
    }

    @Test
    fun colorScheme_jsonDeserialization_fallbacksToDefaults() {
        val jsonString = """
            {
                "primary": "#111111",
                "onPrimary": "#FFFFFF"
            }
        """.trimIndent()
        val json = Json { ignoreUnknownKeys = true }
        val scheme = json.decodeFromString<ColorScheme>(jsonString)

        assertEquals("#FF6B6B", scheme.axisX)
        assertEquals("#7CFFD8", scheme.axisY)
        assertEquals("#6DE8FF", scheme.axisZ)
    }

    @Test
    fun linkpoint2Tokens_axisTokens_matchDefaults() {
        val tokens = Linkpoint2Tokens(
            radii = L2Radii.Default,
            spacing = L2Spacing.Balanced,
            type = L2TypeScale(),
            aurora = L2Aurora(Color.Red, Color.Green, Color.Blue),
            glass = L2Glass(Color.Black, Color.White),
            coordColor = Color.Gray,
            onSurfaceDim = Color.DarkGray,
            outlineSubtle = Color.LightGray,
            success = Color.Green,
            warning = Color.Yellow,
            unreadBadge = Color.Red,
            onUnreadBadge = Color.White
        )

        assertEquals(Color(0xFFFF6B6B), tokens.axisX)
        assertEquals(Color(0xFF7CFFD8), tokens.axisY)
        assertEquals(Color(0xFF6DE8FF), tokens.axisZ)
    }

    @Test
    fun builtInThemes_toKthemeTheme_hasAxisTokens() {
        val themePack = BuiltInThemes.LINKPOINT_DEFAULT
        val ktheme = themePack.toKthemeTheme()
        assertNotNull(ktheme.colorScheme)
        assertEquals("#FF6B6B", ktheme.colorScheme.axisX)
        assertEquals("#7CFFD8", ktheme.colorScheme.axisY)
        assertEquals("#6DE8FF", ktheme.colorScheme.axisZ)
    }
}
