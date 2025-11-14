package com.lumiyaviewer.lumiya.ui.theme

import com.lumiyaviewer.lumiya.ui.compose.theme.ColorUtils
import org.junit.Assert.*
import org.junit.Test
import androidx.compose.ui.graphics.Color

/**
 * Unit tests for ThemeUtils
 */
class ThemeUtilsTest {
    
    @Test
    fun `contrastRatio calculates correct ratio for black and white`() {
        val ratio = ColorUtils.contrastRatio(Color.Black, Color.White)
        assertEquals(21.0f, ratio, 0.1f)
    }
    
    @Test
    fun `contrastRatio calculates correct ratio for same colors`() {
        val ratio = ColorUtils.contrastRatio(Color.Black, Color.Black)
        assertEquals(1.0f, ratio, 0.1f)
    }
    
    @Test
    fun `meetsWCAGAA returns true for sufficient contrast`() {
        assertTrue(ColorUtils.meetsWCAGAA(Color.Black, Color.White))
    }
    
    @Test
    fun `meetsWCAGAA returns false for insufficient contrast`() {
        assertFalse(ColorUtils.meetsWCAGAA(Color.Gray, Color.White))
    }
    
    @Test
    fun `meetsWCAGAAA returns true for high contrast`() {
        assertTrue(ColorUtils.meetsWCAGAAA(Color.Black, Color.White))
    }
    
    @Test
    fun `darken reduces color brightness`() {
        val original = Color.Red
        val darkened = ColorUtils.darken(original, 0.5f)
        
        assertTrue(darkened.red < original.red)
        assertEquals(original.alpha, darkened.alpha, 0.01f)
    }
    
    @Test
    fun `lighten increases color brightness`() {
        val original = Color.Red
        val lightened = ColorUtils.lighten(original, 0.5f)
        
        assertTrue(lightened.red > original.red)
        assertEquals(original.alpha, lightened.alpha, 0.01f)
    }
    
    @Test
    fun `withOpacity changes alpha value`() {
        val original = Color.Red
        val transparent = ColorUtils.withOpacity(original, 0.5f)
        
        assertEquals(0.5f, transparent.alpha, 0.01f)
        assertEquals(original.red, transparent.red, 0.01f)
    }
    
    @Test
    fun `withOpacity clamps alpha to valid range`() {
        val color = Color.Red
        
        val tooLow = ColorUtils.withOpacity(color, -0.5f)
        assertEquals(0.0f, tooLow.alpha, 0.01f)
        
        val tooHigh = ColorUtils.withOpacity(color, 1.5f)
        assertEquals(1.0f, tooHigh.alpha, 0.01f)
    }
}