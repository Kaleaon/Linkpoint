package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.*

/**
 * Chat from simulator message
 */
class ChatFromSimulatorMessage : SLMessage() {
    var fromName: String = ""
    var sourceId: UUID = UUID.randomUUID()
    var ownerId: UUID = UUID.randomUUID()
    var sourceType: Int = 0
    var chatType: Int = 0
    var audible: Int = 0
    var position: LLVector3 = LLVector3()
    var message: String = ""

    private val charset: Charset = Charsets.UTF_8

    override fun packPayload(buffer: ByteBuffer) {
        packVariable(buffer, fromName.toByteArray(charset), 1)
        packUUID(buffer, sourceId)
        packUUID(buffer, ownerId)
        require(sourceType in 0..0xFF) { "sourceType out of range ($sourceType)" }
        require(chatType in 0..0xFF) { "chatType out of range ($chatType)" }
        require(audible in 0..0xFF) { "audible out of range ($audible)" }
        packByte(buffer, sourceType)
        packByte(buffer, chatType)
        packByte(buffer, audible)
        position.pack(buffer)
        packVariable(buffer, message.toByteArray(charset), 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        fromName = String(unpackVariable(buffer, 1), charset)
        sourceId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        sourceType = unpackByte(buffer)
        chatType = unpackByte(buffer)
        audible = unpackByte(buffer)
        position = LLVector3.unpack(buffer)
        message = String(unpackVariable(buffer, 2), charset)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHAT_FROM_SIMULATOR

    override fun getMessageName(): String = "ChatFromSimulator"
}

/**
 * Chat from viewer message
 */
class ChatFromViewerMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var message: String = ""
    var type: Int = 0
    var channel: Int = 0

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packVariable(buffer, message.toByteArray(charset), 2)
        require(type in 0..0xFF) { "type out of range ($type)" }
        packByte(buffer, type)
        packInt(buffer, channel)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        message = String(unpackVariable(buffer, 2), charset)
        type = unpackByte(buffer)
        channel = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHAT_FROM_VIEWER

    override fun getMessageName(): String = "ChatFromViewer"
}

/**
 * Improved IM message
 */
class ImprovedIMMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var fromGroup: Boolean = false
    var toAgentId: UUID = UUID.randomUUID()
    var parentEstateId: Int = 0
    var regionId: UUID = UUID.randomUUID()
    var position: LLVector3 = LLVector3()
    var offline: Int = 0
    var dialog: Int = 0
    var id: UUID = UUID.randomUUID()
    var timestamp: Int = 0
    var fromAgentName: String = ""
    var message: String = ""
    var binaryBucket: ByteArray = ByteArray(0)

    private val charset: Charset = Charsets.UTF_8

    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packBoolean(buffer, fromGroup)
        packUUID(buffer, toAgentId)
        packInt(buffer, parentEstateId)
        packUUID(buffer, regionId)
        position.pack(buffer)
        require(offline in 0..0xFF) { "offline out of range ($offline)" }
        require(dialog in 0..0xFF) { "dialog out of range ($dialog)" }
        packByte(buffer, offline)
        packByte(buffer, dialog)
        packUUID(buffer, id)
        packInt(buffer, timestamp)
        packVariable(buffer, fromAgentName.toByteArray(charset), 1)
        packVariable(buffer, message.toByteArray(charset), 2)
        packVariable(buffer, binaryBucket, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        fromGroup = unpackBoolean(buffer)
        toAgentId = unpackUUID(buffer)
        parentEstateId = unpackInt(buffer)
        regionId = unpackUUID(buffer)
        position = LLVector3.unpack(buffer)
        offline = unpackByte(buffer)
        dialog = unpackByte(buffer)
        id = unpackUUID(buffer)
        timestamp = unpackInt(buffer)
        fromAgentName = String(unpackVariable(buffer, 1), charset)
        message = String(unpackVariable(buffer, 2), charset)
        binaryBucket = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.IMPROVED_IM

    override fun getMessageName(): String = "ImprovedInstantMessage"
}
