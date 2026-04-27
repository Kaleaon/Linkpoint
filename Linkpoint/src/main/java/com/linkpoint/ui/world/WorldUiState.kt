package com.linkpoint.ui.world

data class WorldUiState(
    val regionName: String = "Unknown Region",
    val avatarName: String = "",
    val fps: Int? = null,
    val bandwidthKbps: Int? = null,
    val interactionMode: InteractionMode = InteractionMode.FOLLOW,
    val overlaysVisibility: OverlaysVisibility = OverlaysVisibility()
) {
    data class OverlaysVisibility(
        val topStatusHud: Boolean = true,
        val rightActionStack: Boolean = true,
        val bottomChatPreview: Boolean = true,
        val movementJoystick: Boolean = true,
        val cameraJoystick: Boolean = true,
        val hudAttachments: Boolean = true
    )

    enum class InteractionMode {
        FOLLOW,
        MOUSELOOK
    }
}
