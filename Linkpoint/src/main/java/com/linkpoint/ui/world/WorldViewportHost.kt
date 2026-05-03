package com.linkpoint.ui.world

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.linkpoint.hud.HUDOverlayView
import com.linkpoint.ui.radar.BlipType
import com.linkpoint.ui.radar.Radar
import com.linkpoint.ui.radar.RadarBlip
import com.manalkaff.jetstick.JoyStick

/**
 * Single interop wrapper for world viewport rendering surfaces.
 * All backend surface views are attached through this host.
 */
class WorldViewportHost @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewportContainer = FrameLayout(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    val hudOverlay: HUDOverlayView = HUDOverlayView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        addView(viewportContainer)
        addView(hudOverlay)
    }

    fun attachViewport(view: View) {
        if (view.parent === viewportContainer && viewportContainer.childCount == 1) return
        viewportContainer.removeAllViews()
        viewportContainer.addView(view)
    }

    fun clearViewport() {
        viewportContainer.removeAllViews()
    }

    fun setHudAttachmentsVisible(visible: Boolean) {
        hudOverlay.visibility = if (visible) View.VISIBLE else View.GONE
    }
}

/**
 * Sole world viewport entrypoint that wraps renderer surface + overlay stack.
 */
@Composable
fun WorldViewportHost(
    uiState: WorldComposeUiState,
    uiActions: WorldComposeUiActions,
    rendererHandoffManager: RendererHandoffManager? = null,
    modifier: Modifier = Modifier,
    rendererSurface: @Composable BoxScope.() -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(rendererHandoffManager, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> rendererHandoffManager?.onHostResumed()
                Lifecycle.Event.ON_PAUSE -> rendererHandoffManager?.onHostPaused()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            rendererHandoffManager?.onHostDisposed()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        rendererSurface()
        WorldOverlayContainer(uiState = uiState, uiActions = uiActions, modifier = Modifier.fillMaxSize())
    }
}

/**
 * Fixed world overlay layer order:
 * 1) HUD, 2) Action stack, 3) Chat preview, 4) Joystick, 5) Minimap, 6) Toasts, 7) Modals.
 */
@Composable
fun WorldOverlayContainer(
    uiState: WorldComposeUiState,
    uiActions: WorldComposeUiActions,
    modifier: Modifier = Modifier
) {
    // Radar blips derived from the live AvatarManager. Previously hardcoded
    // sample data ("Friend", "Avatar") shipped to production, which made the
    // radar pretend to detect avatars even when nobody else was around.
    val radarBlips = run {
        val app = com.linkpoint.LinkpointApp.getInstanceOrNull()
        if (app == null || !app.isAvatarManagerInitialized()) {
            emptyList<RadarBlip>()
        } else {
            val me = app.avatarManager.getMyAvatar()
            val mePos = me?.position ?: com.linkpoint.protocol.types.LLVector3.zero()
            val friendIds: Set<java.util.UUID> = if (app.isFriendsManagerInitialized()) {
                app.friendsManager.getAllFriends().map { it.agentId }.toSet()
            } else emptySet()
            app.avatarManager.getNearbyAvatars(mePos, 96f)
                .filter { me == null || it.agentId != me.agentId }
                .map { av ->
                    val dx = av.position.x - mePos.x
                    val dy = av.position.y - mePos.y
                    val dz = av.position.z - mePos.z
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
                    RadarBlip(
                        id = av.agentId.toString(),
                        name = av.agentId.toString().take(8),
                        type = if (av.agentId in friendIds) BlipType.FRIEND else BlipType.STRANGER,
                        distance = dist,
                        bearing = kotlin.math.atan2(dy, dx),
                        altitude = dz,
                    )
                }
        }
    }

    var joystickX by remember { mutableStateOf(0f) }
    var joystickY by remember { mutableStateOf(0f) }

    Box(modifier = modifier) {
        if (uiState.isHudVisible) {
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                color = Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = uiState.avatarName, color = Color.White)
                    Text(text = uiState.regionName, color = Color.White)
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(onClick = uiActions::toggleFly) { Text(if (uiState.isFlying) "Fly" else "Walk") }
            FloatingActionButton(onClick = uiActions::openActionStack) { Text("Act") }
        }

        uiState.chatPreviewText?.let { preview ->
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp),
                color = Color.Black.copy(alpha = 0.65f)
            ) {
                Text(preview, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Color.White)
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            JoyStick(size = 140.dp, dotSize = 48.dp) { x, y ->
                joystickX = x
                joystickY = y
            }
        }

        if (uiState.canOpenMinimap) {
            Radar(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(120.dp),
                range = 96f,
                heading = if (joystickX == 0f && joystickY == 0f) 0f else kotlin.math.atan2(joystickY, joystickX),
                blips = radarBlips,
                enableSweepAnimation = true
            )
        }

        uiState.toastMessage?.let { message ->
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 28.dp),
                color = Color.DarkGray.copy(alpha = 0.9f)
            ) {
                Text(text = message, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Color.White)
            }
        }

        uiState.activeModalTag?.let { modalTag ->
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                color = Color(0xEE111111),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Modal: $modalTag", color = Color.White)
                    FloatingActionButton(onClick = uiActions::dismissModal) { Text("Close") }
                }
            }
        }
    }
}
