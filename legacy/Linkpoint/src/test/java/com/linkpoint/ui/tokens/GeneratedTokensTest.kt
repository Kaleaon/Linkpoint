package com.linkpoint.ui.tokens

import androidx.compose.ui.graphics.Color
import com.linkpoint.ui.components.linkpoint2.tokens.GeneratedTokens
import org.junit.Assert.assertEquals
import org.junit.Test

class GeneratedTokensTest {

    @Test
    fun testStatusTokensParity() {
        assertEquals(Color(0xFF4CAF50), GeneratedTokens.Color.Status.Online)
        assertEquals(Color(0xFFF44336), GeneratedTokens.Color.Status.Offline)
        assertEquals(Color(0xFFFF9800), GeneratedTokens.Color.Status.Degraded)
        assertEquals(Color(0xFF9E9E9E), GeneratedTokens.Color.Status.Unknown)
        assertEquals(Color(0xFF7CFFD8), GeneratedTokens.Color.Status.Success)
        assertEquals(Color(0xFFFFD56D), GeneratedTokens.Color.Status.Warning)
        assertEquals(Color(0xFFFF9800), GeneratedTokens.Color.Status.Unsaved)
    }

    @Test
    fun testEditorTokensParity() {
        assertEquals(Color(0xFF252526), GeneratedTokens.Color.Editor.AppbarBackground)
        assertEquals(Color(0xFF1E1E1E), GeneratedTokens.Color.Editor.Background)
        assertEquals(Color(0xFF2D2D2D), GeneratedTokens.Color.Editor.PanelBackground)
        assertEquals(Color(0xFF3C3C3C), GeneratedTokens.Color.Editor.Divider)
        assertEquals(Color(0xFFD4D4D4), GeneratedTokens.Color.Editor.Text)
        assertEquals(Color(0xFFB0B0B0), GeneratedTokens.Color.Editor.TextMuted)
        assertEquals(Color(0xFF808080), GeneratedTokens.Color.Editor.TextDim)
    }

    @Test
    fun testSceneAndGlassTokensParity() {
        assertEquals(Color(0xFF000000), GeneratedTokens.Color.Scene.Background)
        assertEquals(Color(0x88000000), GeneratedTokens.Color.Scene.Shadow)
        assertEquals(Color(0x386DE8FF), GeneratedTokens.Color.Aurora.Color1)
        assertEquals(Color(0x8C101C33), GeneratedTokens.Color.Glass.Background)
        assertEquals(Color(0x14FFFFFF), GeneratedTokens.Color.Glass.Stroke)
        assertEquals(Color(0xAA000000), GeneratedTokens.Color.HudBackground)
        assertEquals(Color(0x33FFFFFF), GeneratedTokens.Color.HudStroke)
    }
}
