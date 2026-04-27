package com.linkpoint.ui.world

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.viewinterop.AndroidView

private object WorldOverlayZ {
    const val TOP_STATUS = 1f
    const val RIGHT_ACTIONS = 2f
    const val BOTTOM_CHAT = 3f
    const val MOVEMENT = 4f
    const val CAMERA = 4f
}

@Composable
fun WorldOverlay(
    state: WorldUiState,
    movementJoystickFactory: () -> JoystickView,
    cameraJoystickFactory: () -> JoystickView,
    onMenu: () -> Unit,
    onChat: () -> Unit,
    onMinimap: () -> Unit,
    onInventory: () -> Unit,
    onXr: () -> Unit,
    onGestures: () -> Unit,
    onFriends: () -> Unit,
    onNearby: () -> Unit,
    onCameraMode: () -> Unit,
    onFly: () -> Unit,
    onRun: () -> Unit,
    onJump: () -> Unit,
    onSit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.overlaysVisibility.topStatusHud) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(WorldOverlayZ.TOP_STATUS),
                color = Color(0xAA000000)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    Column {
                        Text(state.regionName, color = Color.White, style = MaterialTheme.typography.titleSmall)
                        val perf = "FPS ${state.fps ?: "--"} • BW ${state.bandwidthKbps ?: "--"} kbps"
                        Text(perf, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(state.interactionMode.name, color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (state.overlaysVisibility.rightActionStack) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .zIndex(WorldOverlayZ.RIGHT_ACTIONS)
                    .background(Color(0x55000000), RoundedCornerShape(24.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ActionButton(Icons.Default.Chat, onChat, "Chat")
                ActionButton(Icons.Default.Map, onMinimap, "Minimap")
                ActionButton(Icons.Default.Work, onInventory, "Inventory")
                ActionButton(Icons.Default.ViewInAr, onXr, "XR")
                ActionButton(Icons.Default.SportsEsports, onGestures, "Gestures")
                ActionButton(Icons.Default.Group, onFriends, "Friends")
                ActionButton(Icons.Default.NearMe, onNearby, "Nearby")
                ActionButton(Icons.Default.PhotoCamera, onCameraMode, "Camera")
                ActionButton(Icons.Default.FlightTakeoff, onFly, "Fly")
                ActionButton(Icons.Default.DirectionsRun, onRun, "Run")
                ActionButton(Icons.Default.ArrowUpward, onJump, "Jump")
                ActionButton(Icons.Default.EventSeat, onSit, "Sit")
            }
        }

        if (state.overlaysVisibility.bottomChatPreview) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .zIndex(WorldOverlayZ.BOTTOM_CHAT)
                    .clickable(onClick = onChat),
                color = Color(0xB2000000),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Chat preview • Tap to open",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        if (state.overlaysVisibility.movementJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.MOVEMENT)
                    .size(150.dp),
                factory = { movementJoystickFactory() }
            )
        }

        if (state.overlaysVisibility.cameraJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.CAMERA)
                    .size(120.dp),
                factory = { cameraJoystickFactory() }
            )
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, label: String) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(icon, contentDescription = label, tint = Color.White)
    }
}
