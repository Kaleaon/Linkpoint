package com.linkpoint.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography System for Linkpoint
 * 
 * Defines consistent text styles following Material Design 3 guidelines.
 * All text styles are designed for:
 * - Readability across different screen sizes
 * - Accessibility compliance
 * - Consistent visual hierarchy
 * - Dynamic type support
 */

/**
 * Custom Font Families
 * 
 * Define custom fonts here if needed.
 * Default uses system fonts for better performance and compatibility.
 */
object LinkpointFonts {
    // Using system default fonts for now
    // Can be replaced with custom fonts if needed
    val defaultFontFamily = FontFamily.Default
    val displayFontFamily = FontFamily.Default
    val bodyFontFamily = FontFamily.Default
}

/**
 * Linkpoint Typography
 * 
 * Complete typography scale following Material Design 3.
 */
val LinkpointTypography = Typography(
    // Display styles - Largest text on screen
    displayLarge = TextStyle(
        fontFamily = LinkpointFonts.displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = LinkpointFonts.displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = LinkpointFonts.displayFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    
    // Headline styles - High-emphasis text
    headlineLarge = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Title styles - Medium-emphasis text
    titleLarge = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    
    // Body styles - Main content text
    bodyLarge = TextStyle(
        fontFamily = LinkpointFonts.bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = LinkpointFonts.bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = LinkpointFonts.bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Label styles - UI elements
    labelLarge = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Custom Text Styles
 * 
 * Additional text styles specific to Linkpoint features.
 */
object LinkpointTextStyles {
    
    /**
     * Chat message text style
     */
    val chatMessage = TextStyle(
        fontFamily = LinkpointFonts.bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp
    )
    
    /**
     * Chat timestamp text style
     */
    val chatTimestamp = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
    
    /**
     * Username text style
     */
    val username = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    /**
     * Object name text style
     */
    val objectName = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    
    /**
     * Object description text style
     */
    val objectDescription = TextStyle(
        fontFamily = LinkpointFonts.bodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    )
    
    /**
     * Status text style
     */
    val status = TextStyle(
        fontFamily = LinkpointFonts.defaultFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    
    /**
     * Coordinate text style (for world coordinates)
     */
    val coordinates = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
    
    /**
     * Code/monospace text style
     */
    val code = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp
    )
}

/**
 * Typography Utilities
 */
object TypographyUtils {
    
    /**
     * Minimum accessible text size (12sp)
     */
    const val MIN_TEXT_SIZE_SP = 12f
    
    /**
     * Check if text size is accessible
     */
    fun isAccessible(textSizeSp: Float): Boolean {
        return textSizeSp >= MIN_TEXT_SIZE_SP
    }
    
    /**
     * Scale text size for accessibility
     */
    fun scaleForAccessibility(textSizeSp: Float, scaleFactor: Float): Float {
        val scaled = textSizeSp * scaleFactor
        return if (scaled < MIN_TEXT_SIZE_SP) MIN_TEXT_SIZE_SP else scaled
    }
    
    /**
     * Get recommended line height for text size
     * Rule of thumb: line height = text size * 1.4 to 1.6
     */
    fun recommendedLineHeight(textSizeSp: Float): Float {
        return textSizeSp * 1.5f
    }
}

/**
 * Text Style Extensions
 */

/**
 * Create a bold variant of a text style
 */
fun TextStyle.bold(): TextStyle {
    return this.copy(fontWeight = FontWeight.Bold)
}

/**
 * Create a semi-bold variant of a text style
 */
fun TextStyle.semiBold(): TextStyle {
    return this.copy(fontWeight = FontWeight.SemiBold)
}

/**
 * Create a medium variant of a text style
 */
fun TextStyle.medium(): TextStyle {
    return this.copy(fontWeight = FontWeight.Medium)
}

/**
 * Create an italic variant of a text style
 */
fun TextStyle.italic(): TextStyle {
    return this.copy(fontWeight = FontWeight.Normal)
}

/**
 * Scale text size
 */
fun TextStyle.scale(factor: Float): TextStyle {
    return this.copy(
        fontSize = fontSize * factor,
        lineHeight = lineHeight * factor
    )
}

/**
 * Apply letter spacing
 */
fun TextStyle.withLetterSpacing(spacing: Float): TextStyle {
    return this.copy(letterSpacing = spacing.sp)
}