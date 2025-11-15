package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedInfoUpdateMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var classifiedId: UUID = UUID(0L, 0L)
    var category: Int = 0
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var parcelId: UUID = UUID(0L, 0L)
    var parentEstate: Int = 0
    var snapshotId: UUID = UUID(0L, 0L)
    var posGlobal: LLVector3d = LLVector3d()
    var classifiedFlags: Int = 0
    var priceForListing: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, classifiedId)
        packInt(buffer, category)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 2)
        packUUID(buffer, parcelId)
        packInt(buffer, parentEstate)
        packUUID(buffer, snapshotId)
        posGlobal.pack(buffer)
        packByte(buffer, classifiedFlags)
        packInt(buffer, priceForListing)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        classifiedId = unpackUUID(buffer)
        category = unpackInt(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 2)
        parcelId = unpackUUID(buffer)
        parentEstate = unpackInt(buffer)
        snapshotId = unpackUUID(buffer)
        posGlobal = LLVector3d.unpack(buffer)
        classifiedFlags = unpackByte(buffer)
        priceForListing = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF002D

    override fun getMessageName(): String = "ClassifiedInfoUpdate"
}
