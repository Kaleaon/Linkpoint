package com.linkpoint.ui.linkpoint2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.linkpoint.ui.components.linkpoint2.primitives.L2Chip
import com.linkpoint.ui.components.linkpoint2.primitives.L2ChipVariant
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2WorldSceneBackdrop
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

enum class CameraLens(val label: String) { Wide("0.5x"), Default("1x"), Tele("2x"), Portrait("Portrait") }

/**
 * Cluster C — Camera / photo mode. See design/screens-2.jsx → CameraScreen.
 */
@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onShutter: () -> Unit,
    onSnapToFriends: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var lens by remember { mutableStateOf(CameraLens.Default) }
    val tokens = Linkpoint2.tokens

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        L2WorldSceneBackdrop()

        // Top close
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            L2GlassSurface(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            L2GlassSurface(
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
            ) {
                IconButton(onClick = onSnapToFriends) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Snap to friends", tint = Color.White)
                }
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Lens picker chips
            L2GlassSurface(
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(tokens.radii.pill),
                contentPadding = PaddingValues(6.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CameraLens.entries.forEach { l ->
                        L2Chip(
                            label = l.label,
                            variant = if (l == lens) L2ChipVariant.Primary else L2ChipVariant.Neutral,
                            onClick = { lens = l },
                        )
                    }
                }
            }
            // Shutter row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { /* gallery */ }) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    IconButton(onClick = onShutter, modifier = Modifier.size(72.dp)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Shutter", tint = Color.Black)
                    }
                }
                IconButton(onClick = onFlip) {
                    Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip camera", tint = Color.White)
                }
            }
            Text(
                "${lens.label} · 1/120  f/2.4  ISO 200",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
    }
}
