package com.linkpoint.world3d

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.linkpoint.ui.world.RendererHandoffManager
import com.linkpoint.ui.world.WorldUiActions
import com.linkpoint.ui.world.WorldUiState
import com.linkpoint.ui.world.WorldViewportHost

/**
 * Main 3D World Screen integrating SceneView and world overlay UI.
 */
@Composable
fun WorldScreen(
    worldBridge: WorldBridge,
    rendererHandoffManager: RendererHandoffManager? = null,
    onOpenInventory: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val worldStateData by worldBridge.worldState.collectAsState()
    var isFlying by remember { mutableStateOf(false) }

    val worldState = rememberWorldState().apply {
        cameraPosition = worldStateData.cameraPosition
        cameraTarget = worldStateData.cameraTarget
    }

    val uiState = object : WorldUiState {
        override val regionName: String = "Unknown Region"
        override val avatarName: String = "Resident"
        override val isHudVisible: Boolean = true
        override val isFlying: Boolean = isFlying
        override val canOpenMinimap: Boolean = true
        override val chatPreviewText: String? = null
        override val toastMessage: String? = null
        override val activeModalTag: String? = null
    }

    val uiActions = object : WorldUiActions {
        override fun openChat() {}
        override fun openActionStack() = onOpenInventory()
        override fun openMinimap() = onOpenMap()
        override fun toggleFly() {
            isFlying = !isFlying
            worldBridge.setFlying(isFlying)
        }
        override fun dismissToast() {}
        override fun dismissModal() {}
    }

    WorldViewportHost(
        uiState = uiState,
        uiActions = uiActions,
        rendererHandoffManager = rendererHandoffManager,
        modifier = Modifier.fillMaxSize()
    ) {
        WorldScene(
            modifier = Modifier.fillMaxSize(),
            worldState = worldState,
            onModelTapped = {},
            onWorldTapped = {},
            onFrameUpdate = {}
        )
    }
}
