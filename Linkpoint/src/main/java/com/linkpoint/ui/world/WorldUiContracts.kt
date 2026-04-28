package com.linkpoint.ui.world

import com.linkpoint.LinkpointApp

/**
 * Compose-facing world UI snapshot.
 *
 * The interface intentionally mirrors existing manager/service backed values so
 * callers can adapt whatever backing implementation they currently use.
 */
interface WorldComposeUiState {
    val regionName: String
    val avatarName: String
    val isHudVisible: Boolean
    val isFlying: Boolean
    val canOpenMinimap: Boolean
    val chatPreviewText: String?
    val toastMessage: String?
    val activeModalTag: String?
}

/**
 * Compose-facing world UI actions.
 */
interface WorldComposeUiActions {
    fun openChat()
    fun openActionStack()
    fun openMinimap()
    fun toggleFly()
    fun dismissToast()
    fun dismissModal()
}

/**
 * Adapter backed by existing app/service managers.
 */
class ManagerBackedWorldComposeUiState(
    private val app: LinkpointApp,
    override val isHudVisible: Boolean = true,
    override val isFlying: Boolean = false,
    override val chatPreviewText: String? = null,
    override val toastMessage: String? = null,
    override val activeModalTag: String? = null
) : WorldComposeUiState {
    override val regionName: String
        get() = app.sessionManager.currentRegion.value?.name ?: "Unknown Region"

    override val avatarName: String
        get() = app.sessionManager.getAvatarName()

    override val canOpenMinimap: Boolean
        get() = app.isMinimapManagerInitialized()
}

class ManagerBackedWorldComposeUiActions(
    private val onOpenChat: () -> Unit,
    private val onOpenActionStack: () -> Unit,
    private val onOpenMinimap: () -> Unit,
    private val onToggleFly: () -> Unit,
    private val onDismissToast: () -> Unit = {},
    private val onDismissModal: () -> Unit = {}
) : WorldComposeUiActions {
    override fun openChat() = onOpenChat()
    override fun openActionStack() = onOpenActionStack()
    override fun openMinimap() = onOpenMinimap()
    override fun toggleFly() = onToggleFly()
    override fun dismissToast() = onDismissToast()
    override fun dismissModal() = onDismissModal()
}
