package com.linkpoint.ui.minimap

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Minimap avatar marker
 */
data class MinimapMarker(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val isFriend: Boolean = false,
    val isGroup: Boolean = false
)

/**
 * Compose version of MinimapActivity.
 * 
 * Features:
 * - Region minimap display
 * - Avatar positions with heading indicator
 * - Zoom controls
 * - Tap to open world map
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimapScreen(
    regionName: String,
    avatarPosition: Offset,
    avatarHeading: Float,  // In radians
    markers: List<MinimapMarker>,
    onNavigateBack: () -> Unit,
    onOpenWorldMap: () -> Unit,
    onMarkerTapped: (MinimapMarker) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoom by remember { mutableFloatStateOf(1f) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(regionName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenWorldMap) {
                        Icon(Icons.Default.Map, contentDescription = "World Map")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Minimap canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A2A1A))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Check if tap is near a marker
                            val canvasWidth = size.width.toFloat()
                            val canvasHeight = size.height.toFloat()
                            val scale = minOf(canvasWidth, canvasHeight) / 256f * zoom
                            
                            markers.forEach { marker ->
                                val markerX = canvasWidth / 2 + (marker.x - avatarPosition.x) * scale
                                val markerY = canvasHeight / 2 - (marker.y - avatarPosition.y) * scale
                                
                                if ((offset.x - markerX) * (offset.x - markerX) + 
                                    (offset.y - markerY) * (offset.y - markerY) < 400) {
                                    onMarkerTapped(marker)
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2
                val scale = minOf(canvasWidth, canvasHeight) / 256f * zoom
                
                // Draw grid
                val gridSpacing = 10f * scale
                val gridColor = Color(0xFF2A4A2A)
                
                for (i in -25..25) {
                    val offset = i * gridSpacing
                    // Vertical lines
                    drawLine(
                        color = gridColor,
                        start = Offset(centerX + offset, 0f),
                        end = Offset(centerX + offset, canvasHeight),
                        strokeWidth = 1f
                    )
                    // Horizontal lines
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, centerY + offset),
                        end = Offset(canvasWidth, centerY + offset),
                        strokeWidth = 1f
                    )
                }
                
                // Draw parcel boundaries (simulated)
                drawRect(
                    color = Color(0xFF4A6A4A),
                    topLeft = Offset(centerX - 128 * scale, centerY - 128 * scale),
                    size = androidx.compose.ui.geometry.Size(256 * scale, 256 * scale),
                    style = Stroke(width = 2f)
                )
                
                // Draw markers (other avatars)
                markers.forEach { marker ->
                    val markerX = centerX + (marker.x - avatarPosition.x) * scale
                    val markerY = centerY - (marker.y - avatarPosition.y) * scale
                    
                    val color = when {
                        marker.isFriend -> Color.Yellow
                        marker.isGroup -> Color.Cyan
                        else -> Color.Green
                    }
                    
                    drawCircle(
                        color = color,
                        radius = 6f,
                        center = Offset(markerX, markerY)
                    )
                }
                
                // Draw self (center) with heading indicator
                rotate(
                    degrees = -avatarHeading * (180f / Math.PI.toFloat()),
                    pivot = Offset(centerX, centerY)
                ) {
                    // Triangle pointing up
                    val path = Path().apply {
                        moveTo(centerX, centerY - 12f)
                        lineTo(centerX - 8f, centerY + 8f)
                        lineTo(centerX + 8f, centerY + 8f)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = Color.White
                    )
                }
                
                // Draw compass directions
                val compassRadius = minOf(canvasWidth, canvasHeight) / 2 - 20f
                listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f).forEach { (label, angle) ->
                    val radians = Math.toRadians(angle.toDouble() - 90)
                    val x = centerX + (compassRadius * cos(radians)).toFloat()
                    val y = centerY + (compassRadius * sin(radians)).toFloat()
                    
                    drawCircle(
                        color = if (label == "N") Color.Red else Color.White.copy(alpha = 0.5f),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
            }
            
            // Zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { zoom = (zoom * 1.5f).coerceAtMost(4f) }
                ) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                }
                
                Spacer(modifier = Modifier.size(8.dp))
                
                SmallFloatingActionButton(
                    onClick = { zoom = (zoom / 1.5f).coerceAtLeast(0.5f) }
                ) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                }
            }
            
            // Position info
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "(${avatarPosition.x.toInt()}, ${avatarPosition.y.toInt()})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
