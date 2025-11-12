package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class PickInfoReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var pickId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var topPick: Boolean = false
    var parcelId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var snapshotId: UUID = UUID(0L, 0L)
    var user: ByteArray = ByteArray(0)
    var originalName: ByteArray = ByteArray(0)
    var simName: ByteArray = ByteArray(0)
    var posGlobal: LLVector3d = LLVector3d()
    var sortOrder: Int = 0
    var enabled: Boolean = false


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, pickId)
        packUUID(buffer, creatorId)
        packBoolean(buffer, topPick)
        packUUID(buffer, parcelId)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 2)
        packUUID(buffer, snapshotId)
        packVariable(buffer, user, 1)
        packVariable(buffer, originalName, 1)
        packVariable(buffer, simName, 1)
        posGlobal.pack(buffer)
        packInt(buffer, sortOrder)
        packBoolean(buffer, enabled)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        pickId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        topPick = unpackBoolean(buffer)
        parcelId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 2)
        snapshotId = unpackUUID(buffer)
        user = unpackVariable(buffer, 1)
        originalName = unpackVariable(buffer, 1)
        simName = unpackVariable(buffer, 1)
        posGlobal = LLVector3d.unpack(buffer)
        sortOrder = unpackInt(buffer)
        enabled = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00B8

    override fun getMessageName(): String = "PickInfoReply"
}
