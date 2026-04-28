package com.linkpoint.ui.world

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.linkpoint.ui.components.linkpoint2.primitives.L2GlassSurface
import com.linkpoint.ui.components.linkpoint2.primitives.L2HudTag
import com.linkpoint.ui.components.linkpoint2.tokens.Linkpoint2

private object WorldOverlayZ {
    const val TOP_STATUS = 1f
    const val RIGHT_ACTIONS = 2f
    const val BOTTOM_CHAT = 3f
    const val MOVEMENT = 4f
    const val CAMERA = 4f
}

/**
 * Linkpoint 2.0 world HUD overlay. Renders on top of the GL viewport:
 * - Top: glass status pill with region name, coord, fps/bandwidth tags.
 * - Right: vertical glass action stack (chat, minimap, inventory, fly, sit…).
 * - Bottom-left: chat preview pill.
 * - Bottom corners: movement & camera joysticks.
 *
 * See design/screens-1.jsx → WorldScreen for the visual reference.
 */
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
    onSit: () -> Unit,
) {
    val tokens = Linkpoint2.tokens
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.overlaysVisibility.topStatusHud) {
            L2GlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter)
                    .zIndex(WorldOverlayZ.TOP_STATUS),
                shape = RoundedCornerShape(tokens.radii.lg),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(
                            text = state.regionName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Text(
                            text = "FPS ${state.fps ?: "--"}  ·  ${state.bandwidthKbps ?: "--"} kbps",
                            color = tokens.coordColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                        )
                    }
                    L2HudTag {
                        Text(
                            text = state.interactionMode.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        if (state.overlaysVisibility.rightActionStack) {
            L2GlassSurface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .zIndex(WorldOverlayZ.RIGHT_ACTIONS),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(6.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HudActionButton(Icons.Default.Chat, onChat, "Chat")
                    HudActionButton(Icons.Default.Map, onMinimap, "Minimap")
                    HudActionButton(Icons.Default.Work, onInventory, "Inventory")
                    HudActionButton(Icons.Default.ViewInAr, onXr, "XR")
                    HudActionButton(Icons.Default.SportsEsports, onGestures, "Gestures")
                    HudActionButton(Icons.Default.Group, onFriends, "Friends")
                    HudActionButton(Icons.Default.NearMe, onNearby, "Nearby")
                    HudActionButton(Icons.Default.PhotoCamera, onCameraMode, "Camera")
                    HudActionButton(Icons.Default.FlightTakeoff, onFly, "Fly")
                    HudActionButton(Icons.Default.DirectionsRun, onRun, "Run")
                    HudActionButton(Icons.Default.ArrowUpward, onJump, "Jump")
                    HudActionButton(Icons.Default.EventSeat, onSit, "Sit")
                }
            }
        }

        if (state.overlaysVisibility.bottomChatPreview) {
            L2GlassSurface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .zIndex(WorldOverlayZ.BOTTOM_CHAT),
                shape = RoundedCornerShape(tokens.radii.pill),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(tokens.radii.pill))
                        .background(Color.Transparent),
                ) {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Chat preview · Tap to open",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        if (state.overlaysVisibility.movementJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.MOVEMENT)
                    .size(150.dp),
                factory = { movementJoystickFactory() },
            )
        }

        if (state.overlaysVisibility.cameraJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.CAMERA)
                    .size(120.dp),
                factory = { cameraJoystickFactory() },
            )
        }
    }
}

@Composable
private fun HudActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    label: String,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape),
    ) {
        Icon(icon, contentDescription = label, tint = Color.White)
    }
    Spacer(Modifier.height(0.dp))
}
