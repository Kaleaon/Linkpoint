package com.linkpoint.ui.world

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
    const val JOYSTICKS = 4f
}

/**
 * Resolved sizes for the overlay derived from the current viewport.
 *
 * Computed by [WorldOverlay] once per layout pass via [BoxWithConstraints] so
 * the HUD stays balanced on small phones (~320dp), normal phones, foldables
 * unfolded (~600dp+) and tablets (~800dp+) without manual breakpoints in each
 * sub-composable.
 */
private data class OverlayMetrics(
    val movementJoystick: Dp,
    val cameraJoystick: Dp,
    val railIcon: Dp,
    val railTop: Dp,
    val railBottom: Dp,
    val chatBottom: Dp,
    val chatMaxWidth: Dp,
    val edgePad: Dp,
)

private fun overlayMetricsFor(width: Dp, height: Dp): OverlayMetrics {
    val w = width.value
    val h = height.value
    // Joystick diameter scales with screen width but clamps at sane ergonomic
    // bounds — small phones get smaller sticks so they don't dominate the
    // viewport, tablets cap at 200dp so they don't become hand-size targets.
    val movement = (w * 0.30f).coerceIn(110f, 200f).dp
    val camera = (w * 0.24f).coerceIn(90f, 170f).dp
    // Rail clearances follow the joystick height so the rail always finishes
    // above the sticks, even in compact landscape.
    val railBottom = (movement.value + 32f).coerceAtLeast(150f).dp
    val railTop = if (h < 600f) 56.dp else 80.dp
    val chatBottom = (movement.value + 56f).coerceAtLeast(170f).dp
    val chatMax = (w * 0.7f).coerceIn(220f, 480f).dp
    val edgePad = if (w >= 600f) 20.dp else 12.dp
    val railIcon = if (w >= 600f) 48.dp else 40.dp
    return OverlayMetrics(
        movementJoystick = movement,
        cameraJoystick = camera,
        railIcon = railIcon,
        railTop = railTop,
        railBottom = railBottom,
        chatBottom = chatBottom,
        chatMaxWidth = chatMax,
        edgePad = edgePad,
    )
}

private data class HudAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Linkpoint 2.0 world HUD overlay. Renders on top of the GL viewport:
 * - Top: glass status pill with region name, FPS/bandwidth, interaction-mode tag.
 * - Right: vertical glass action rail with primary actions + expandable "More".
 * - Bottom-center: chat preview pill.
 * - Bottom corners: movement & camera joysticks.
 *
 * The rail is intentionally short by default (5 primary actions) so it does
 * not crowd the viewport or fight the joysticks at the bottom; the secondary
 * actions live behind a chevron toggle. The whole rail is also scrollable as
 * a fallback for very small screens.
 *
 * See design/screens-2.jsx → WorldScreen for the visual reference.
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
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val metrics = overlayMetricsFor(maxWidth, maxHeight)

        if (state.overlaysVisibility.topStatusHud) {
            TopStatusBar(
                state = state,
                onMenu = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = metrics.edgePad, vertical = 12.dp)
                    .align(Alignment.TopCenter)
                    .zIndex(WorldOverlayZ.TOP_STATUS),
            )
        }

        if (state.overlaysVisibility.rightActionStack) {
            val primary = remember(onChat, onMinimap, onInventory, onFriends, onCameraMode) {
                listOf(
                    HudAction(Icons.AutoMirrored.Filled.Chat, "Chat", onChat),
                    HudAction(Icons.Default.Map, "Minimap", onMinimap),
                    HudAction(Icons.Default.Work, "Inventory", onInventory),
                    HudAction(Icons.Default.Group, "Friends", onFriends),
                    HudAction(Icons.Default.PhotoCamera, "Camera", onCameraMode),
                )
            }
            val secondary = remember(onXr, onGestures, onNearby, onFly, onRun, onJump, onSit) {
                listOf(
                    HudAction(Icons.Default.ViewInAr, "XR", onXr),
                    HudAction(Icons.Default.SportsEsports, "Gestures", onGestures),
                    HudAction(Icons.Default.NearMe, "Nearby", onNearby),
                    HudAction(Icons.Default.FlightTakeoff, "Fly", onFly),
                    HudAction(Icons.AutoMirrored.Filled.DirectionsRun, "Run", onRun),
                    HudAction(Icons.Default.ArrowUpward, "Jump", onJump),
                    HudAction(Icons.Default.EventSeat, "Sit", onSit),
                )
            }
            RightActionRail(
                primary = primary,
                secondary = secondary,
                iconSize = metrics.railIcon,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = metrics.railTop, bottom = metrics.railBottom, end = metrics.edgePad)
                    .zIndex(WorldOverlayZ.RIGHT_ACTIONS),
            )
        }

        if (state.overlaysVisibility.bottomChatPreview) {
            BottomChatPreview(
                maxWidth = metrics.chatMaxWidth,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = metrics.chatBottom, start = metrics.edgePad, end = metrics.edgePad)
                    .zIndex(WorldOverlayZ.BOTTOM_CHAT),
            )
        }

        if (state.overlaysVisibility.movementJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = metrics.edgePad, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.JOYSTICKS)
                    .size(metrics.movementJoystick),
                factory = { movementJoystickFactory() },
            )
        }

        if (state.overlaysVisibility.cameraJoystick) {
            AndroidView(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = metrics.edgePad, bottom = 24.dp)
                    .zIndex(WorldOverlayZ.JOYSTICKS)
                    .size(metrics.cameraJoystick),
                factory = { cameraJoystickFactory() },
            )
        }
    }
}

@Composable
private fun TopStatusBar(
    state: WorldUiState,
    onMenu: () -> Unit,
    modifier: Modifier,
) {
    val tokens = Linkpoint2.tokens
    L2GlassSurface(
        modifier = modifier,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "FPS ${state.fps ?: "--"}  ·  ${state.bandwidthKbps ?: "--"} kbps",
                    color = tokens.coordColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            L2HudTag {
                Text(
                    text = state.interactionMode.shortLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}

@Composable
private fun RightActionRail(
    primary: List<HudAction>,
    secondary: List<HudAction>,
    iconSize: Dp,
    modifier: Modifier,
) {
    val tokens = Linkpoint2.tokens
    var expanded by remember { mutableStateOf(false) }
    L2GlassSurface(
        modifier = modifier,
        shape = RoundedCornerShape(tokens.radii.xl),
        contentPadding = PaddingValues(6.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            primary.forEach { HudActionButton(it, iconSize) }
            HudActionToggle(expanded = expanded, iconSize = iconSize, onToggle = { expanded = !expanded })
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    secondary.forEach { HudActionButton(it, iconSize) }
                }
            }
        }
    }
}

@Composable
private fun HudActionButton(action: HudAction, size: Dp) {
    IconButton(
        onClick = action.onClick,
        modifier = Modifier.size(size),
    ) {
        Icon(action.icon, contentDescription = action.label, tint = Color.White)
    }
}

@Composable
private fun HudActionToggle(expanded: Boolean, iconSize: Dp, onToggle: () -> Unit) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier.size(iconSize),
    ) {
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (expanded) "Show fewer actions" else "Show more actions",
            tint = Color.White,
        )
    }
}

@Composable
private fun BottomChatPreview(maxWidth: Dp, modifier: Modifier) {
    val tokens = Linkpoint2.tokens
    L2GlassSurface(
        modifier = modifier.widthIn(max = maxWidth),
        shape = RoundedCornerShape(tokens.radii.pill),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Chat preview · Tap to open",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
