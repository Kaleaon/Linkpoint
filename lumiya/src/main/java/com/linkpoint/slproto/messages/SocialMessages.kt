package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Accept calling card message (0xFFFF012E)
 */
class AcceptCallingCardMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val folderIds: MutableList<UUID> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
        require(folderIds.size in 0..0xFF) { "folderIds size out of range (${folderIds.size})" }
        packByte(buffer, folderIds.size)
        folderIds.forEach { packUUID(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        folderIds.clear()
        repeat(unpackByte(buffer)) {
            folderIds += unpackUUID(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ACCEPT_CALLING_CARD

    override fun getMessageName(): String = "AcceptCallingCard"
}

/**
 * Decline calling card message (0xFFFF012F)
 */
class DeclineCallingCardMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DECLINE_CALLING_CARD

    override fun getMessageName(): String = "DeclineCallingCard"
}

/**
 * Offer calling card message (0xFFFF012D)
 */
class OfferCallingCardMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, destId)
        packUUID(buffer, transactionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.OFFER_CALLING_CARD

    override fun getMessageName(): String = "OfferCallingCard"
}

/**
 * Accept friendship message (0xFFFF0129)
 */
class AcceptFriendshipMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    val folderIds: MutableList<UUID> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
        require(folderIds.size in 0..0xFF) { "folderIds size out of range (${folderIds.size})" }
        packByte(buffer, folderIds.size)
        folderIds.forEach { packUUID(buffer, it) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        folderIds.clear()
        repeat(unpackByte(buffer)) {
            folderIds += unpackUUID(buffer)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ACCEPT_FRIENDSHIP

    override fun getMessageName(): String = "AcceptFriendship"
}

/**
 * Decline friendship message (0xFFFF012A)
 */
class DeclineFriendshipMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DECLINE_FRIENDSHIP

    override fun getMessageName(): String = "DeclineFriendship"
}

/**
 * Form friendship message (0xFFFF012B)
 */
class FormFriendshipMessage : SLMessage() {
    var sourceId: UUID = UUID(0L, 0L)
    var destId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, sourceId)
        packUUID(buffer, destId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sourceId = unpackUUID(buffer)
        destId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.FORM_FRIENDSHIP

    override fun getMessageName(): String = "FormFriendship"
}

/**
 * Terminate friendship message (0xFFFF012C)
 */
class TerminateFriendshipMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var otherId: UUID = UUID(0L, 0L)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, otherId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        otherId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TERMINATE_FRIENDSHIP

    override fun getMessageName(): String = "TerminateFriendship"
}

/**
 * Change user rights message (0xFFFF0141)
 */
class ChangeUserRightsMessage : SLMessage() {
    data class RightsEntry(
        var agentRelated: UUID = UUID(0L, 0L),
        var relatedRights: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    val rights: MutableList<RightsEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        require(rights.size in 0..0xFF) { "rights size out of range (${rights.size})" }
        packByte(buffer, rights.size)
        rights.forEach { entry ->
            packUUID(buffer, entry.agentRelated)
            packInt(buffer, entry.relatedRights)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        rights.clear()
        repeat(unpackByte(buffer)) {
            rights += RightsEntry(
                agentRelated = unpackUUID(buffer),
                relatedRights = unpackInt(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHANGE_USER_RIGHTS

    override fun getMessageName(): String = "ChangeUserRights"
}

/**
 * Grant user rights message (0xFFFF0140)
 */
class GrantUserRightsMessage : SLMessage() {
    data class RightsEntry(
        var agentRelated: UUID = UUID(0L, 0L),
        var relatedRights: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    val rights: MutableList<RightsEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        require(rights.size in 0..0xFF) { "rights size out of range (${rights.size})" }
        packByte(buffer, rights.size)
        rights.forEach { entry ->
            packUUID(buffer, entry.agentRelated)
            packInt(buffer, entry.relatedRights)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        rights.clear()
        repeat(unpackByte(buffer)) {
            rights += RightsEntry(
                agentRelated = unpackUUID(buffer),
                relatedRights = unpackInt(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.GRANT_USER_RIGHTS

    override fun getMessageName(): String = "GrantUserRights"
}

/**
 * Activate gestures message (0xFFFF013C)
 */
class ActivateGesturesMessage : SLMessage() {
    data class GestureEntry(
        var itemId: UUID = UUID(0L, 0L),
        var assetId: UUID = UUID(0L, 0L),
        var gestureFlags: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    val gestures: MutableList<GestureEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        require(gestures.size in 0..0xFF) { "gestures size out of range (${gestures.size})" }
        packByte(buffer, gestures.size)
        gestures.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packUUID(buffer, entry.assetId)
            packInt(buffer, entry.gestureFlags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        gestures.clear()
        repeat(unpackByte(buffer)) {
            gestures += GestureEntry(
                itemId = unpackUUID(buffer),
                assetId = unpackUUID(buffer),
                gestureFlags = unpackInt(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ACTIVATE_GESTURES

    override fun getMessageName(): String = "ActivateGestures"
}

/**
 * Deactivate gestures message (0xFFFF013D)
 */
class DeactivateGesturesMessage : SLMessage() {
    data class GestureEntry(
        var itemId: UUID = UUID(0L, 0L),
        var gestureFlags: Int = 0,
    )

    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    val gestures: MutableList<GestureEntry> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        require(gestures.size in 0..0xFF) { "gestures size out of range (${gestures.size})" }
        packByte(buffer, gestures.size)
        gestures.forEach { entry ->
            packUUID(buffer, entry.itemId)
            packInt(buffer, entry.gestureFlags)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        gestures.clear()
        repeat(unpackByte(buffer)) {
            gestures += GestureEntry(
                itemId = unpackUUID(buffer),
                gestureFlags = unpackInt(buffer),
            )
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.DEACTIVATE_GESTURES

    override fun getMessageName(): String = "DeactivateGestures"
}

/**
 * Activate group message (0xFFFF0170)
 */
class ActivateGroupMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.ACTIVATE_GROUP

    override fun getMessageName(): String = "ActivateGroup"
}

/**
 * Generic message (0xFFFF0105)
 */
class GenericMessageMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var method: ByteArray = ByteArray(0)
    var invoiceId: UUID = UUID(0L, 0L)
    val parameters: MutableList<ByteArray> = mutableListOf()

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, transactionId)
        packVariable(buffer, method, 1)
        packUUID(buffer, invoiceId)
        require(parameters.size in 0..0xFF) { "parameters size out of range (${parameters.size})" }
        packByte(buffer, parameters.size)
        parameters.forEach { param -> packVariable(buffer, param, 1) }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        method = unpackVariable(buffer, 1)
        invoiceId = unpackUUID(buffer)
        parameters.clear()
        repeat(unpackByte(buffer)) {
            parameters += unpackVariable(buffer, 1)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.GENERIC_MESSAGE

    override fun getMessageName(): String = "GenericMessage"
}
