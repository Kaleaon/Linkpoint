package com.linkpoint.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Region data for map display
 */
data class MapRegion(
    val name: String,
    val x: Int,
    val y: Int,
    val isOnline: Boolean = true,
    val access: RegionAccess = RegionAccess.GENERAL
)

enum class RegionAccess {
    GENERAL,
    MODERATE,
    ADULT
}

/**
 * Map marker for points of interest
 */
data class MapMarker(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val type: MarkerType
)

enum class MarkerType {
    SELF,
    FRIEND,
    LANDMARK,
    TELEPORT_HISTORY
}

/**
 * Compose version of MapActivity.
 * 
 * Features:
 * - Grid map display
 * - Pan and zoom gestures
 * - Region info on tap
 * - Teleport to location
 * - Search by region name
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    currentRegion: String,
    currentPosition: Offset,
    regions: List<MapRegion> = emptyList(),
    markers: List<MapMarker> = emptyList(),
    onNavigateBack: () -> Unit,
    onTeleportTo: (MapRegion) -> Unit,
    onTeleportHome: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var zoom by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedRegion by remember { mutableStateOf<MapRegion?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("World Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            // Map canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            zoom = (zoom * gestureZoom).coerceIn(0.5f, 4f)
                            offset += pan
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // Convert tap to world coordinates
                            val worldX = ((tapOffset.x - offset.x) / zoom / 256f).toInt()
                            val worldY = ((tapOffset.y - offset.y) / zoom / 256f).toInt()
                            
                            selectedRegion = regions.find { it.x == worldX && it.y == worldY }
                        }
                    }
            ) {
                val gridSize = 256f * zoom
                
                translate(offset.x, offset.y) {
                    // Draw grid
                    val startX = (-offset.x / gridSize).toInt() - 1
                    val endX = ((size.width - offset.x) / gridSize).toInt() + 1
                    val startY = (-offset.y / gridSize).toInt() - 1
                    val endY = ((size.height - offset.y) / gridSize).toInt() + 1
                    
                    for (x in startX..endX) {
                        for (y in startY..endY) {
                            val region = regions.find { it.x == x && it.y == y }
                            val color = when {
                                region == null -> Color(0xFF1A1A1A)  // No region
                                !region.isOnline -> Color(0xFF333333)  // Offline
                                region.access == RegionAccess.ADULT -> Color(0xFF442222)
                                region.access == RegionAccess.MODERATE -> Color(0xFF334422)
                                else -> Color(0xFF224444)  // General
                            }
                            
                            drawRect(
                                color = color,
                                topLeft = Offset(x * gridSize, y * gridSize),
                                size = androidx.compose.ui.geometry.Size(gridSize - 1, gridSize - 1)
                            )
                        }
                    }
                    
                    // Draw markers
                    markers.forEach { marker ->
                        val markerColor = when (marker.type) {
                            MarkerType.SELF -> Color.Green
                            MarkerType.FRIEND -> Color.Yellow
                            MarkerType.LANDMARK -> Color.Cyan
                            MarkerType.TELEPORT_HISTORY -> Color.Magenta
                        }
                        
                        drawCircle(
                            color = markerColor,
                            radius = 8f * zoom,
                            center = Offset(marker.x * gridSize, marker.y * gridSize)
                        )
                    }
                }
            }
            
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    onSearch(it)
                },
                label = { Text("Search Region") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                singleLine = true
            )
            
            // Zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
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
                
                Spacer(modifier = Modifier.size(16.dp))
                
                SmallFloatingActionButton(
                    onClick = { 
                        // Center on current position
                        offset = Offset.Zero
                        zoom = 1f
                    }
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                }
                
                Spacer(modifier = Modifier.size(8.dp))
                
                SmallFloatingActionButton(
                    onClick = onTeleportHome
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Teleport Home")
                }
            }
            
            // Current location info
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currentRegion (${currentPosition.x.toInt()}, ${currentPosition.y.toInt()})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            // Selected region info
            selectedRegion?.let { region ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = region.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "(${region.x}, ${region.y}) - ${region.access.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        FloatingActionButton(
                            onClick = { onTeleportTo(region) },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Teleport")
                        }
                    }
                }
            }
        }
    }
}
