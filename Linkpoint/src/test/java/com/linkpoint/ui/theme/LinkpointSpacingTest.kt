package com.linkpoint.ui.theme

import androidx.compose.ui.unit.dp
import com.ktheme.models.Adaptation
import com.ktheme.models.LayoutAdaptation
import com.ktheme.models.Theme
import com.ktheme.models.ThemeMetadata
import com.ktheme.models.ColorScheme
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkpointSpacingTest {

    @Test
    fun testSpacingComputesFromKthemeAdaptationTokens() {
        val dummyColorScheme = ColorScheme(
            primary = "#000000",
            onPrimary = "#FFFFFF"
        )

        // Test compact density with 1.5x spacing scale
        val compactTheme = Theme(
            metadata = ThemeMetadata(id = "compact_test"),
            colorScheme = dummyColorScheme,
            adaptation = Adaptation(
                layout = LayoutAdaptation(
                    density = "compact",
                    spacingScale = 1.5f
                )
            )
        )
        val compactPack = compactTheme.toThemePack()
        assertEquals(DensityProfile.COMPACT, compactPack.resolvedDensityProfile())
        val compactSpacing = compactPack.toSpacing()
        assertEquals((2 * 1.5f).dp, compactSpacing.xxs)
        assertEquals((4 * 1.5f).dp, compactSpacing.xs)
        assertEquals((6 * 1.5f).dp, compactSpacing.sm)
        assertEquals((10 * 1.5f).dp, compactSpacing.md)

        // Test comfortable density with default spacing scale (1.0)
        val comfortableTheme = Theme(
            metadata = ThemeMetadata(id = "comfortable_test"),
            colorScheme = dummyColorScheme,
            adaptation = Adaptation(
                layout = LayoutAdaptation(
                    density = "comfortable",
                    spacingScale = 1.0f
                )
            )
        )
        val comfortablePack = comfortableTheme.toThemePack()
        assertEquals(DensityProfile.COMFORTABLE, comfortablePack.resolvedDensityProfile())
        val comfortableSpacing = comfortablePack.toSpacing()
        assertEquals(6.dp, comfortableSpacing.xxs)
        assertEquals(10.dp, comfortableSpacing.xs)
        assertEquals(18.dp, comfortableSpacing.md)
    }
}
