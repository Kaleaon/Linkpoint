package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Mono-spaced coord text (e.g. region position, fps, L$ amount).
 */
@Composable
fun L2CoordText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    fontSize: TextUnit? = null,
) {
    val tokens = Linkpoint2.tokens
    Text(
        text = text,
        modifier = modifier,
        color = color ?: tokens.coordColor,
        fontSize = fontSize ?: tokens.type.coord,
        fontFamily = FontFamily.Monospace,
        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
    )
}

/**
 * Display title with the design's display weight.
 */
@Composable
fun L2DisplayText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    fontSize: TextUnit? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color ?: MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize ?: Linkpoint2.tokens.type.titleDisplay,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.displaySmall,
    )
}
