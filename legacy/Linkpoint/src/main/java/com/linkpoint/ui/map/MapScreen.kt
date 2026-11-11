package com.linkpoint.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.linkpoint.inventory.Landmark
import com.linkpoint.ui.common.UiLoadState
import com.linkpoint.ui.common.UiTelemetryEvents
import com.linkpoint.ui.common.logUiTelemetry
import com.linkpoint.ui.components.linkpoint2.primitives.L2TopBar
import com.linkpoint.ui.components.state.EmptyState
import com.linkpoint.ui.components.state.ErrorState
import com.linkpoint.ui.components.state.LoadingState
import com.linkpoint.ui.components.state.LowBandwidthOverlay
import com.linkpoint.ui.components.state.ReconnectingBanner
import java.util.UUID
import kotlin.math.floor
import kotlin.math.sqrt

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

data class MapTeleportTarget(
    val title: String,
    val description: String = "",
    val regionName: String,
    val regionX: Int,
    val regionY: Int,
    val localX: Float,
    val localY: Float,
    val localZ: Float = 25f,
    val access: RegionAccess = RegionAccess.GENERAL,
    val isLandmark: Boolean = false,
    val landmarkId: UUID? = null
)

private data class MaturityRatingInfo(
    val label: String,
    val warningText: String,
    val containerColor: Color,
    val contentColor: Color
)

fun getLandmarkGridPosition(lm: Landmark, regions: List<MapRegion>): Pair<Float, Float> {
    if (lm.regionHandle != 0L) {
        val regX = ((lm.regionHandle ushr 32) and 0xFFFFFFFFL) / 256f
        val regY = (lm.regionHandle and 0xFFFFFFFFL) / 256f
        return Pair(regX + lm.position.x / 256f, regY + lm.position.y / 256f)
    }
    val matched = regions.find { it.name.equals(lm.regionName, ignoreCase = true) }
    if (matched != null) {
        return Pair(matched.x + lm.position.x / 256f, matched.y + lm.position.y / 256f)
    }
    return Pair(lm.position.x / 256f, lm.position.y / 256f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    currentRegion: String,
    currentPosition: Offset,
    currentRegionGridX: Int = 1000,
    currentRegionGridY: Int = 1000,
    regions: List<MapRegion> = emptyList(),
    markers: List<MapMarker> = emptyList(),
    landmarks: List<Landmark> = emptyList(),
    uiLoadState: UiLoadState = UiLoadState.Content,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    onTeleportTo: (MapRegion) -> Unit = {},
    onTeleportToLocation: (regionName: String, x: Float, y: Float, z: Float) -> Unit = { _, _, _, _ -> },
    onTeleportHome: () -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var zoom by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedTarget by remember { mutableStateOf<MapTeleportTarget?>(null) }

    Scaffold(
        topBar = {
            L2TopBar(
                title = "World map",
                subtitle = "Pan & teleport",
                leading = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiLoadState is UiLoadState.Reconnecting) {
                ReconnectingBanner(
                    message = uiLoadState.message,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            when (uiLoadState) {
                is UiLoadState.Loading -> LoadingState(message = uiLoadState.message)
                is UiLoadState.Error -> ErrorState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    retryLabel = uiLoadState.retryLabel,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "map", uiLoadState)
                        onRetry()
                    }
                )
                is UiLoadState.Empty -> EmptyState(
                    title = uiLoadState.title,
                    message = uiLoadState.message,
                    actionLabel = uiLoadState.actionLabel,
                    onAction = onRetry
                )
                UiLoadState.Content, is UiLoadState.Reconnecting, is UiLoadState.LowBandwidth -> {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, gestureZoom, _ ->
                                    zoom = (zoom * gestureZoom).coerceIn(0.5f, 4f)
                                    offset += pan
                                }
                            }
                            .pointerInput(landmarks, regions) {
                                detectTapGestures { tapOffset ->
                                    val gridSize = 256f * zoom
                                    val tapGridX = (tapOffset.x - offset.x) / gridSize
                                    val tapGridY = (tapOffset.y - offset.y) / gridSize

                                    // 1. Hit test against saved landmark pins (hit threshold ~24px screen distance)
                                    val maxHitDistGrid = 24f / gridSize
                                    var closestLandmark: Landmark? = null
                                    var closestDist = Float.MAX_VALUE

                                    landmarks.forEach { lm ->
                                        val (lmGridX, lmGridY) = getLandmarkGridPosition(lm, regions)
                                        val dx = tapGridX - lmGridX
                                        val dy = tapGridY - lmGridY
                                        val dist = sqrt(dx * dx + dy * dy)
                                        if (dist <= maxHitDistGrid && dist < closestDist) {
                                            closestDist = dist
                                            closestLandmark = lm
                                        }
                                    }

                                    if (closestLandmark != null) {
                                        val lm = closestLandmark!!
                                        val (lmGridX, lmGridY) = getLandmarkGridPosition(lm, regions)
                                        val lmRegX = lmGridX.toInt()
                                        val lmRegY = lmGridY.toInt()
                                        val matchedRegion = regions.find { it.x == lmRegX && it.y == lmRegY }
                                            ?: regions.find { it.name.equals(lm.regionName, ignoreCase = true) }

                                        selectedTarget = MapTeleportTarget(
                                            title = lm.name.ifBlank { "Landmark" },
                                            description = lm.description,
                                            regionName = if (lm.regionName.isNotBlank()) lm.regionName else (matchedRegion?.name ?: "Region ($lmRegX, $lmRegY)"),
                                            regionX = lmRegX,
                                            regionY = lmRegY,
                                            localX = lm.position.x,
                                            localY = lm.position.y,
                                            localZ = lm.position.z,
                                            access = matchedRegion?.access ?: RegionAccess.GENERAL,
                                            isLandmark = true,
                                            landmarkId = lm.itemId
                                        )
                                    } else {
                                        // 2. Region tile tap with sub-region local coordinates
                                        val regX = floor(tapGridX).toInt()
                                        val regY = floor(tapGridY).toInt()
                                        val locX = ((tapGridX - regX) * 256f).coerceIn(0f, 256f)
                                        val locY = ((tapGridY - regY) * 256f).coerceIn(0f, 256f)

                                        val matchedRegion = regions.find { it.x == regX && it.y == regY }

                                        selectedTarget = MapTeleportTarget(
                                            title = matchedRegion?.name ?: "Region ($regX, $regY)",
                                            regionName = matchedRegion?.name ?: "Region ($regX, $regY)",
                                            regionX = regX,
                                            regionY = regY,
                                            localX = locX,
                                            localY = locY,
                                            localZ = 25f,
                                            access = matchedRegion?.access ?: RegionAccess.GENERAL,
                                            isLandmark = false
                                        )
                                    }
                                }
                            }
                    ) {
                        val gridSize = 256f * zoom
                        translate(offset.x, offset.y) {
                            val startX = (-offset.x / gridSize).toInt() - 1
                            val endX = ((size.width - offset.x) / gridSize).toInt() + 1
                            val startY = (-offset.y / gridSize).toInt() - 1
                            val endY = ((size.height - offset.y) / gridSize).toInt() + 1

                            for (x in startX..endX) {
                                for (y in startY..endY) {
                                    val region = regions.find { it.x == x && it.y == y }
                                    val color = when {
                                        region == null -> Color(0xFF1A1A1A)
                                        !region.isOnline -> Color(0xFF333333)
                                        region.access == RegionAccess.ADULT -> Color(0xFF442222)
                                        region.access == RegionAccess.MODERATE -> Color(0xFF334422)
                                        else -> Color(0xFF224444)
                                    }
                                    drawRect(
                                        color = color,
                                        topLeft = Offset(x * gridSize, y * gridSize),
                                        size = androidx.compose.ui.geometry.Size(gridSize - 1, gridSize - 1)
                                    )
                                }
                            }

                            // Render generic map markers
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

                            // Render saved landmark pins
                            landmarks.forEach { lm ->
                                val (lmGridX, lmGridY) = getLandmarkGridPosition(lm, regions)
                                val pinCenterX = lmGridX * gridSize
                                val pinCenterY = lmGridY * gridSize
                                val pinRadius = (8f * zoom).coerceIn(6f, 18f)
                                val isSelected = selectedTarget?.landmarkId == lm.itemId

                                if (isSelected) {
                                    // Selection highlight ring
                                    drawCircle(
                                        color = Color.Cyan.copy(alpha = 0.4f),
                                        radius = pinRadius * 1.8f,
                                        center = Offset(pinCenterX, pinCenterY)
                                    )
                                    drawCircle(
                                        color = Color.Cyan,
                                        radius = pinRadius * 1.4f,
                                        center = Offset(pinCenterX, pinCenterY),
                                        style = Stroke(width = 2f * zoom)
                                    )
                                }

                                // Main cyan pin outer head
                                drawCircle(
                                    color = Color.Cyan,
                                    radius = pinRadius,
                                    center = Offset(pinCenterX, pinCenterY)
                                )
                                // Dark inner dot for contrast
                                drawCircle(
                                    color = Color(0xFF003344),
                                    radius = pinRadius * 0.45f,
                                    center = Offset(pinCenterX, pinCenterY)
                                )
                            }
                        }
                    }

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

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(16.dp)
                    ) {
                        SmallFloatingActionButton(onClick = { zoom = (zoom * 1.5f).coerceAtMost(4f) }) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        SmallFloatingActionButton(onClick = { zoom = (zoom / 1.5f).coerceAtLeast(0.5f) }) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        SmallFloatingActionButton(onClick = {
                            offset = Offset.Zero
                            zoom = 1f
                        }) {
                            Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        SmallFloatingActionButton(onClick = onTeleportHome) {
                            Icon(Icons.Default.Home, contentDescription = "Teleport Home")
                        }
                    }

                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$currentRegion (${currentPosition.x.toInt()}, ${currentPosition.y.toInt()})")
                        }
                    }

                    selectedTarget?.let { target ->
                        val avatarGlobalX = currentRegionGridX * 256f + currentPosition.x
                        val avatarGlobalY = currentRegionGridY * 256f + currentPosition.y
                        val targetGlobalX = target.regionX * 256f + target.localX
                        val targetGlobalY = target.regionY * 256f + target.localY
                        val dx = targetGlobalX - avatarGlobalX
                        val dy = targetGlobalY - avatarGlobalY
                        val distMeters = sqrt(dx * dx + dy * dy)
                        val distStr = if (distMeters < 1000f) {
                            "${distMeters.toInt()} m"
                        } else {
                            "%.2f km".format(distMeters / 1000f)
                        }

                        val ratingInfo = when (target.access) {
                            RegionAccess.GENERAL -> MaturityRatingInfo(
                                label = "General (G)",
                                warningText = "General Region - Suitable for all audiences.",
                                containerColor = Color(0xFF1E3A1E),
                                contentColor = Color(0xFF81C784)
                            )
                            RegionAccess.MODERATE -> MaturityRatingInfo(
                                label = "Moderate (M)",
                                warningText = "Moderate Region - May contain mature themes.",
                                containerColor = Color(0xFF3E2723),
                                contentColor = Color(0xFFFFB74D)
                            )
                            RegionAccess.ADULT -> MaturityRatingInfo(
                                label = "Adult (A)",
                                warningText = "Adult Region - 18+ content. Adult verification required.",
                                containerColor = Color(0xFF3B1A1A),
                                contentColor = Color(0xFFE57373)
                            )
                        }

                        AlertDialog(
                            onDismissRequest = { selectedTarget = null },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (target.isLandmark) Icons.Default.Place else Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = if (target.isLandmark) Color.Cyan else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Column {
                                        Text(
                                            text = if (target.isLandmark) target.title else "Teleport to ${target.regionName}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (target.isLandmark && target.regionName.isNotBlank()) {
                                            Text(
                                                text = target.regionName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            },
                            text = {
                                Column {
                                    if (target.description.isNotBlank()) {
                                        Text(
                                            text = target.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Grid Coordinates",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "(${target.regionX}, ${target.regionY})",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Local Position",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "(${target.localX.toInt()}, ${target.localY.toInt()}, ${target.localZ.toInt()})",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Distance",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = distStr,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = ratingInfo.containerColor,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Maturity Rating",
                                                tint = ratingInfo.contentColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Maturity: ${ratingInfo.label}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = ratingInfo.contentColor
                                                )
                                                Text(
                                                    text = ratingInfo.warningText,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ratingInfo.contentColor.copy(alpha = 0.9f)
                                                )
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        val t = target
                                        selectedTarget = null
                                        onTeleportToLocation(t.regionName, t.localX, t.localY, t.localZ)
                                        val matchedReg = regions.find { it.x == t.regionX && it.y == t.regionY }
                                            ?: MapRegion(t.regionName, t.regionX, t.regionY, access = t.access)
                                        onTeleportTo(matchedReg)
                                    }
                                ) {
                                    Text("Teleport")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { selectedTarget = null }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }

            if (uiLoadState is UiLoadState.LowBandwidth) {
                LowBandwidthOverlay(
                    message = uiLoadState.message,
                    onRetry = {
                        logUiTelemetry(UiTelemetryEvents.RETRY_TAPPED, "map", uiLoadState)
                        onRetry()
                    }
                )
            }
        }
    }
}
