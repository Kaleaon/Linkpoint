package com.linkpoint.world3d

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.manalkaff.jetstick.JoyStick
import com.linkpoint.ui.radar.Radar
import com.linkpoint.ui.radar.RadarBlip
import com.linkpoint.ui.radar.BlipType
import dev.romainguy.kotlin.math.Float3

/**
 * Main 3D World Screen integrating SceneView, libGDX, and Compose UI.
 * 
 * Layout:
 * - Full-screen 3D world view (SceneView/Filament)
 * - Joystick overlay (bottom-left) using JetStick
 * - Mini radar (top-right)
 * - Action buttons (bottom-right)
 * - HUD info (top-left)
 */
@Composable
fun WorldScreen(
    worldBridge: WorldBridge,
    onOpenInventory: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val worldStateData by worldBridge.worldState.collectAsState()
    val context = LocalContext.current
    
    // World state for SceneView
    val worldState = rememberWorldState().apply {
        cameraPosition = worldStateData.cameraPosition
        cameraTarget = worldStateData.cameraTarget
    }
    
    // Sample radar blips (would come from network in real app)
    val radarBlips = remember {
        listOf(
            RadarBlip("1", "Friend", BlipType.FRIEND, 30f, 0.5f, 0f),
            RadarBlip("2", "Avatar", BlipType.STRANGER, 50f, 2f, 5f)
        )
    }
    
    var isFlying by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 3D World View (SceneView/Filament)
        WorldScene(
            modifier = Modifier.fillMaxSize(),
            worldState = worldState,
            onModelTapped = { model ->
                // Handle model tap
            },
            onWorldTapped = { position ->
                // Handle world tap
            },
            onFrameUpdate = { frameTimeNanos ->
                // Sync with libGDX game loop
            }
        )
        
        // HUD Overlay
        WorldHUD(
            avatarPosition = worldStateData.avatarPosition,
            worldTime = worldStateData.worldTime,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )
        
        // Mini Radar (top-right)
        Radar(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(120.dp),
            range = 96f,
            heading = worldStateData.avatarRotation * (Math.PI.toFloat() / 180f),
            blips = radarBlips,
            enableSweepAnimation = true
        )
        
        // Joystick (bottom-left)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            JoyStick(
                modifier = Modifier,
                size = 150.dp,
                dotSize = 50.dp
            ) { x, y ->
                worldBridge.onJoystickInput(x, y)
            }
        }
        
        // Action Buttons (bottom-right)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fly button
            FloatingActionButton(
                onClick = {
                    isFlying = !isFlying
                    worldBridge.setFlying(isFlying)
                },
                containerColor = if (isFlying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = "Toggle Flying"
                )
            }
            
            // Map button
            FloatingActionButton(
                onClick = onOpenMap,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Open Map"
                )
            }
            
            // Profile button
            FloatingActionButton(
                onClick = onOpenProfile,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Open Profile"
                )
            }
            
            // Settings button
            FloatingActionButton(
                onClick = onOpenSettings,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings"
                )
            }
        }
    }
}

/**
 * HUD overlay showing avatar info and world state
 */
@Composable
private fun WorldHUD(
    avatarPosition: Float3,
    worldTime: Float,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Position: ${avatarPosition.x.toInt()}, ${avatarPosition.y.toInt()}, ${avatarPosition.z.toInt()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Text(
                text = "Time: ${formatWorldTime(worldTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

/**
 * Format world time as HH:MM
 */
private fun formatWorldTime(timeSeconds: Float): String {
    val hours = ((timeSeconds / 3600f) % 24f).toInt()
    val minutes = ((timeSeconds / 60f) % 60f).toInt()
    return "%02d:%02d".format(hours, minutes)
}
