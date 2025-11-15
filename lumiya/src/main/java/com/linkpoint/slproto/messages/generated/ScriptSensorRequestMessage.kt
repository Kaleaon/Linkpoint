package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ScriptSensorRequestMessage : SLMessage() {
    var sourceId: UUID = UUID(0L, 0L)
    var requestId: UUID = UUID(0L, 0L)
    var searchId: UUID = UUID(0L, 0L)
    var searchPos: LLVector3 = LLVector3()
    var searchDir: LLQuaternion = LLQuaternion()
    var searchName: ByteArray = ByteArray(0)
    var type: Int = 0
    var range: Float = 0f
    var arc: Float = 0f
    var regionHandle: Long = 0L
    var searchRegions: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, sourceId)
        packUUID(buffer, requestId)
        packUUID(buffer, searchId)
        searchPos.pack(buffer)
        searchDir.pack(buffer)
        packVariable(buffer, searchName, 1)
        packInt(buffer, type)
        packFloat(buffer, range)
        packFloat(buffer, arc)
        packLong(buffer, regionHandle)
        packByte(buffer, searchRegions)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sourceId = unpackUUID(buffer)
        requestId = unpackUUID(buffer)
        searchId = unpackUUID(buffer)
        searchPos = LLVector3.unpack(buffer)
        searchDir = LLQuaternion.unpack(buffer)
        searchName = unpackVariable(buffer, 1)
        type = unpackInt(buffer)
        range = unpackFloat(buffer)
        arc = unpackFloat(buffer)
        regionHandle = unpackLong(buffer)
        searchRegions = unpackByte(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00F7

    override fun getMessageName(): String = "ScriptSensorRequest"
}
