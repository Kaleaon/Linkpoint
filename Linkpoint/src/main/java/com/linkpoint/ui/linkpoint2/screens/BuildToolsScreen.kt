package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2WorldSceneBackdrop
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

enum class BuildTool { Move, Rotate, Scale, Edit, Texture }

/**
 * Cluster H — Build tools. See design/screens-extra.jsx → BuildToolsScreen.
 */
@Composable
fun BuildToolsScreen(
    selectionName: String = "Selected object",
    selectionMeta: String = "Single prim · 0.50 m³",
    initialTool: BuildTool = BuildTool.Move,
    initialPosition: Triple<Float, Float, Float> = Triple(128f, 64f, 32f),
    onClose: () -> Unit,
    onApply: (BuildTool, Triple<Float, Float, Float>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var tool by remember { mutableStateOf(initialTool) }
    var x by remember { mutableStateOf(initialPosition.first.toString()) }
    var y by remember { mutableStateOf(initialPosition.second.toString()) }
    var z by remember { mutableStateOf(initialPosition.third.toString()) }
    var phantom by remember { mutableStateOf(false) }
    var physical by remember { mutableStateOf(false) }
    val tokens = Linkpoint2.tokens

    Box(modifier = modifier.fillMaxSize()) {
        L2WorldSceneBackdrop()
        // Selection bracket overlay
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(180.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
        )

        // Bottom edit sheet
        L2GlassSurface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(topStart = tokens.radii.xl, topEnd = tokens.radii.xl, bottomStart = tokens.radii.lg, bottomEnd = tokens.radii.lg),
            contentPadding = PaddingValues(16.dp),
        ) {
            Column {
                // Header row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(tokens.radii.md))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            selectionName,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            selectionMeta,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onSurfaceDim,
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Tool tab chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    BuildTool.entries.forEach { t ->
                        L2Chip(
                            label = t.name,
                            variant = if (t == tool) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                            onClick = { tool = t },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                // XYZ inputs (color-coded)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    XyzField("X", x, Color(0xFFFF6B6B)) { x = it }
                    XyzField("Y", y, Color(0xFF7CFFD8)) { y = it }
                    XyzField("Z", z, Color(0xFF6DE8FF)) { z = it }
                }

                Spacer(Modifier.height(12.dp))
                // Phantom / Physical toggles
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Phantom", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Switch(checked = phantom, onCheckedChange = { phantom = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Physical", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Switch(checked = physical, onCheckedChange = { physical = it })
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { /* properties */ },
                        label = { Text("Properties") },
                        leadingIcon = { Icon(Icons.Default.OpenInFull, contentDescription = null) },
                        colors = AssistChipDefaults.assistChipColors(),
                    )
                    AssistChip(
                        onClick = { /* scripts */ },
                        label = { Text("Scripts") },
                        leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    )
                    AssistChip(
                        onClick = { /* content */ },
                        label = { Text("Content") },
                        leadingIcon = { Icon(Icons.Default.Texture, contentDescription = null) },
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun XyzField(
    label: String,
    value: String,
    accent: Color,
    onChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(Linkpoint2.radii.md))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
        Spacer(Modifier.width(6.dp))
        Text(label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(4.dp))
        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.width(72.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
        )
    }
}

