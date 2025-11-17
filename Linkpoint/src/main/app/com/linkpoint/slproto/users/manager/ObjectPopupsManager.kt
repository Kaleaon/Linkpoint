package com.linkpoint.slproto.users.manager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Centralises the logic for interactive object popups (touch menus, payments,
 * and detail sheets). The manager keeps lightweight state objects so the UI
 * can simply observe popup events.
 */
class ObjectPopupsManager(
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val activePopups = ConcurrentHashMap<UUID, PopupState>()
    private val eventFlow = MutableSharedFlow<ObjectPopupEvent>(
        replay = 0,
        extraBufferCapacity = 32,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun events(): SharedFlow<ObjectPopupEvent> = eventFlow

    fun showPopup(metadata: PopupMetadata) {
        val popupState = PopupState(metadata)
        activePopups[metadata.objectId] = popupState
        emit(ObjectPopupEvent.PopupShown(metadata))
    }

    fun dismissPopup(objectId: UUID) {
        val removed = activePopups.remove(objectId) ?: return
        emit(ObjectPopupEvent.PopupDismissed(removed.metadata.objectId))
    }

    fun performAction(objectId: UUID, action: PopupAction) {
        val state = activePopups[objectId] ?: return
        emit(ObjectPopupEvent.ActionInvoked(state.metadata, action))
    }

    fun updateDetails(objectId: UUID, details: ObjectDetails) {
        val state = activePopups.computeIfAbsent(objectId) {
            PopupState(PopupMetadata(objectId = objectId, name = details.name, owner = details.owner))
        }
        state.metadata = state.metadata.copy(
            name = details.name,
            owner = details.owner,
            description = details.description ?: state.metadata.description
        )
        emit(ObjectPopupEvent.DetailsUpdated(state.metadata, details))
    }

    private fun emit(event: ObjectPopupEvent) {
        scope.launch {
            eventFlow.emit(event)
        }
    }

    data class PopupMetadata(
        val objectId: UUID,
        val name: String,
        val owner: String,
        val description: String? = null,
        val payOptions: List<Int> = listOf(1, 5, 10, 50),
        val actions: List<PopupAction> = listOf(
            PopupAction.Touch,
            PopupAction.MoreInfo
        )
    )

    data class ObjectDetails(
        val name: String,
        val owner: String,
        val description: String? = null,
        val permissions: List<String> = emptyList(),
        val creator: String? = null
    )

    sealed class PopupAction(val analyticsName: String) {
        object Touch : PopupAction("touch")
        data class Pay(val amount: Int) : PopupAction("pay")
        object MoreInfo : PopupAction("details")
        object Sit : PopupAction("sit")
    }

    sealed class ObjectPopupEvent {
        data class PopupShown(val metadata: PopupMetadata) : ObjectPopupEvent()
        data class PopupDismissed(val objectId: UUID) : ObjectPopupEvent()
        data class ActionInvoked(val metadata: PopupMetadata, val action: PopupAction) : ObjectPopupEvent()
        data class DetailsUpdated(val metadata: PopupMetadata, val details: ObjectDetails) : ObjectPopupEvent()
    }

    private data class PopupState(var metadata: PopupMetadata)
}
