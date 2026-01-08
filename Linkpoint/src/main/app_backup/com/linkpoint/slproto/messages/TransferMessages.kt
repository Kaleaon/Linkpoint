package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * TransferRequest message (0x68).
 */
class TransferRequestMessage : SLMessage() {
    var transferId: UUID = UUID.randomUUID()
    var channelType: Int = 0
    var sourceType: Int = 0
    var priority: Float = 0f
    var params: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transferId)
        packInt(buffer, channelType)
        packInt(buffer, sourceType)
        packFloat(buffer, priority)
        packVariable(buffer, params, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transferId = unpackUUID(buffer)
        channelType = unpackInt(buffer)
        sourceType = unpackInt(buffer)
        priority = unpackFloat(buffer)
        params = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_REQUEST

    override fun getMessageName(): String = "TransferRequest"
}

/**
 * TransferInfo message (0x69).
 */
class TransferInfoMessage : SLMessage() {
    var transferId: UUID = UUID.randomUUID()
    var channelType: Int = 0
    var targetType: Int = 0
    var status: Int = 0
    var size: Int = 0
    var params: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transferId)
        packInt(buffer, channelType)
        packInt(buffer, targetType)
        packInt(buffer, status)
        packInt(buffer, size)
        packVariable(buffer, params, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transferId = unpackUUID(buffer)
        channelType = unpackInt(buffer)
        targetType = unpackInt(buffer)
        status = unpackInt(buffer)
        size = unpackInt(buffer)
        params = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_INFO

    override fun getMessageName(): String = "TransferInfo"
}

/**
 * TransferPacket message (0x6A).
 */
class TransferPacketMessage : SLMessage() {
    var transferId: UUID = UUID.randomUUID()
    var channelType: Int = 0
    var packet: Int = 0
    var status: Int = 0
    var data: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        packByte(buffer, 0x11)
        packUUID(buffer, transferId)
        packInt(buffer, channelType)
        packInt(buffer, packet)
        packInt(buffer, status)
        packVariable(buffer, data, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // The first byte is ignored per legacy implementation (padding)
        unpackByte(buffer) // consume reserved byte
        transferId = unpackUUID(buffer)
        channelType = unpackInt(buffer)
        packet = unpackInt(buffer)
        status = unpackInt(buffer)
        data = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_PACKET

    override fun getMessageName(): String = "TransferPacket"
}

/**
 * TransferAbort message (0x6B).
 */
class TransferAbortMessage : SLMessage() {
    var transferId: UUID = UUID.randomUUID()
    var channelType: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, transferId)
        packInt(buffer, channelType)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        transferId = unpackUUID(buffer)
        channelType = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TRANSFER_ABORT

    override fun getMessageName(): String = "TransferAbort"
}
