package com.linkpoint.chat

import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID

/**
 * State of action cards in chat (social offers, teleport lure, inventory offer).
 */
enum class CardActionState {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED
}

/**
 * Polymorphic chat event hierarchy representing decoded IM and local chat events.
 */
sealed class SLChatEvent {
    abstract val id: UUID
    abstract val sessionId: UUID
    abstract val fromAgentId: UUID
    abstract val fromName: String
    abstract val message: String
    abstract val dialogType: IMType
    abstract val timestamp: Long
    abstract val isOutgoing: Boolean

    /**
     * Standard chat or IM message.
     */
    data class Text(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.SESSION_SEND,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false
    ) : SLChatEvent()

    /**
     * Friendship invitation offer card event (Dialog 38: IM_FRIENDSHIP_OFFERED).
     */
    data class FriendshipOffer(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.FRIENDSHIP_OFFERED,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false,
        var actionState: CardActionState = CardActionState.PENDING
    ) : SLChatEvent()

    /**
     * Friendship response result (Dialog 39: ACCEPTED, Dialog 40: DECLINED).
     */
    data class FriendshipResult(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false,
        val isAccepted: Boolean
    ) : SLChatEvent()

    /**
     * Group invitation offer card event (Dialog 3: IM_GROUP_INVITATION).
     */
    data class GroupInvitation(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.GROUP_INVITATION,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false,
        val groupId: UUID,
        val joinFee: Int = 0,
        var actionState: CardActionState = CardActionState.PENDING
    ) : SLChatEvent()

    /**
     * Teleport lure card event (Dialog 22: IM_LURE_USER, Dialog 25: IM_GODLIKE_LURE_USER).
     */
    data class TeleportLure(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.LURE_USER,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false,
        val lureId: UUID,
        val regionName: String = "",
        val position: LLVector3 = LLVector3(),
        var actionState: CardActionState = CardActionState.PENDING
    ) : SLChatEvent()

    /**
     * Inventory offer card event (Dialog 4: IM_INVENTORY_OFFERED, Dialog 9: IM_TASK_INVENTORY_OFFERED).
     */
    data class InventoryOffer(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.INVENTORY_OFFERED,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false,
        val itemId: UUID = UUID(0L, 0L),
        var actionState: CardActionState = CardActionState.PENDING
    ) : SLChatEvent()

    /**
     * System or alert notice event.
     */
    data class System(
        override val id: UUID = UUID.randomUUID(),
        override val sessionId: UUID,
        override val fromAgentId: UUID,
        override val fromName: String,
        override val message: String,
        override val dialogType: IMType = IMType.NOTHING_SPECIAL,
        override val timestamp: Long,
        override val isOutgoing: Boolean = false
    ) : SLChatEvent()
}
