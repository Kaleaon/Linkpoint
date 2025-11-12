package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ImprovedInstantMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var fromGroup: Boolean = false
    var toAgentId: UUID = UUID(0L, 0L)
    var parentEstateId: Int = 0
    var regionId: UUID = UUID(0L, 0L)
    var position: LLVector3 = LLVector3()
    var offline: Int = 0
    var dialog: Int = 0
    var id: UUID = UUID(0L, 0L)
    var timestamp: Int = 0
    var fromAgentName: ByteArray = ByteArray(0)
    var message: ByteArray = ByteArray(0)
    var binaryBucket: ByteArray = ByteArray(0)
    var estateId: Int = 0
    val metaData: MutableList<MetaDataBlock> = mutableListOf()

    data class MetaDataBlock(
        var data: ByteArray = ByteArray(0)
    )


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
        packByte(buffer, offline)
        packByte(buffer, dialog)
        packUUID(buffer, id)
        packInt(buffer, timestamp)
        packVariable(buffer, fromAgentName, 1)
        packVariable(buffer, message, 2)
        packVariable(buffer, binaryBucket, 2)
        packInt(buffer, estateId)
        require(metaData.size <= 0xFF) { "MetaData size exceeds 255 (" + metaData.size + ")" }
        packByte(buffer, metaData.size)
        metaData.forEach { entry ->
            packVariable(buffer, entry.data, 2)
        }
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
        fromAgentName = unpackVariable(buffer, 1)
        message = unpackVariable(buffer, 2)
        binaryBucket = unpackVariable(buffer, 2)
        estateId = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            metaData.clear()
            repeat(count) {
                val entry = MetaDataBlock()
                entry.data = unpackVariable(buffer, 2)
                metaData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF00FE

    override fun getMessageName(): String = "ImprovedInstantMessage"
}
