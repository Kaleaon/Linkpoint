package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectDeGrabMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var localId: Int = 0
    val surfaceInfo: MutableList<SurfaceInfoBlock> = mutableListOf()

    data class SurfaceInfoBlock(
        var uvCoord: LLVector3 = LLVector3(),
        var stCoord: LLVector3 = LLVector3(),
        var faceIndex: Int = 0,
        var position: LLVector3 = LLVector3(),
        var normal: LLVector3 = LLVector3(),
        var binormal: LLVector3 = LLVector3()
    )


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, localId)
        require(surfaceInfo.size <= 0xFF) { "SurfaceInfo size exceeds 255 (" + surfaceInfo.size + ")" }
        packByte(buffer, surfaceInfo.size)
        surfaceInfo.forEach { entry ->
            entry.uvCoord.pack(buffer)
            entry.stCoord.pack(buffer)
            packInt(buffer, entry.faceIndex)
            entry.position.pack(buffer)
            entry.normal.pack(buffer)
            entry.binormal.pack(buffer)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        localId = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            surfaceInfo.clear()
            repeat(count) {
                val entry = SurfaceInfoBlock()
                entry.uvCoord = LLVector3.unpack(buffer)
                entry.stCoord = LLVector3.unpack(buffer)
                entry.faceIndex = unpackInt(buffer)
                entry.position = LLVector3.unpack(buffer)
                entry.normal = LLVector3.unpack(buffer)
                entry.binormal = LLVector3.unpack(buffer)
                surfaceInfo += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0077.toInt()

    override fun getMessageName(): String = "ObjectDeGrab"
}
