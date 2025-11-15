package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class PickInfoUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var pickId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var topPick: Boolean = false
    var parcelId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var snapshotId: UUID = UUID(0L, 0L)
    var posGlobal: LLVector3d = LLVector3d()
    var sortOrder: Int = 0
    var enabled: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, pickId)
        packUUID(buffer, creatorId)
        packBoolean(buffer, topPick)
        packUUID(buffer, parcelId)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 2)
        packUUID(buffer, snapshotId)
        posGlobal.pack(buffer)
        packInt(buffer, sortOrder)
        packBoolean(buffer, enabled)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        pickId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        topPick = unpackBoolean(buffer)
        parcelId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 2)
        snapshotId = unpackUUID(buffer)
        posGlobal = LLVector3d.unpack(buffer)
        sortOrder = unpackInt(buffer)
        enabled = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00B9

    override fun getMessageName(): String = "PickInfoUpdate"
}
