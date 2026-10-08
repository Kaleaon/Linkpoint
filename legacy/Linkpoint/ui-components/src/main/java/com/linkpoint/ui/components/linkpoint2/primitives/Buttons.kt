package com.linkpoint.ui.components.linkpoint2.primitives

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

/**
 * Linkpoint 2.0 pill-style filled button. Uses the active theme primary
 * color and the radius profile from [Linkpoint2.radii] so sharp themes
 * (LCARS, Metro, Art Deco) automatically render as squared buttons.
 */
@Composable
fun L2FilledButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 44.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val radii = Linkpoint2.radii
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(radii.pill),
        modifier = modifier.height(height),
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun L2GhostButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 44.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val radii = Linkpoint2.radii
    val outline = Linkpoint2.tokens.outlineSubtle
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(radii.pill),
        border = BorderStroke(1.dp, outline),
        modifier = modifier.height(height),
        contentPadding = contentPadding,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
        content = content,
    )
}

@Composable
fun L2TonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 44.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit,
) {
    val radii = Linkpoint2.radii
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(radii.pill),
        modifier = modifier.height(height),
        contentPadding = contentPadding,
        content = content,
    )
}

/**
 * Convenience overload that just shows text.
 */
@Composable
fun L2FilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = L2FilledButton(onClick = onClick, modifier = modifier, enabled = enabled) { Text(text) }

@Composable
fun L2GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = L2GhostButton(onClick = onClick, modifier = modifier, enabled = enabled) { Text(text) }

@Composable
fun L2TonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) = L2TonalButton(onClick = onClick, modifier = modifier, enabled = enabled) { Text(text) }
