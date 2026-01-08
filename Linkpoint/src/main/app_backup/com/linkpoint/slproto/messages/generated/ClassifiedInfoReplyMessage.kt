package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3d
import java.nio.ByteBuffer
import java.util.UUID

class ClassifiedInfoReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var classifiedId: UUID = UUID(0L, 0L)
    var creatorId: UUID = UUID(0L, 0L)
    var creationDate: Int = 0
    var expirationDate: Int = 0
    var category: Int = 0
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var parcelId: UUID = UUID(0L, 0L)
    var parentEstate: Int = 0
    var snapshotId: UUID = UUID(0L, 0L)
    var simName: ByteArray = ByteArray(0)
    var posGlobal: LLVector3d = LLVector3d()
    var parcelName: ByteArray = ByteArray(0)
    var classifiedFlags: Int = 0
    var priceForListing: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, classifiedId)
        packUUID(buffer, creatorId)
        packInt(buffer, creationDate)
        packInt(buffer, expirationDate)
        packInt(buffer, category)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 2)
        packUUID(buffer, parcelId)
        packInt(buffer, parentEstate)
        packUUID(buffer, snapshotId)
        packVariable(buffer, simName, 1)
        posGlobal.pack(buffer)
        packVariable(buffer, parcelName, 1)
        packByte(buffer, classifiedFlags)
        packInt(buffer, priceForListing)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        classifiedId = unpackUUID(buffer)
        creatorId = unpackUUID(buffer)
        creationDate = unpackInt(buffer)
        expirationDate = unpackInt(buffer)
        category = unpackInt(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 2)
        parcelId = unpackUUID(buffer)
        parentEstate = unpackInt(buffer)
        snapshotId = unpackUUID(buffer)
        simName = unpackVariable(buffer, 1)
        posGlobal = LLVector3d.unpack(buffer)
        parcelName = unpackVariable(buffer, 1)
        classifiedFlags = unpackByte(buffer)
        priceForListing = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF002C.toInt()

    override fun getMessageName(): String = "ClassifiedInfoReply"
}
