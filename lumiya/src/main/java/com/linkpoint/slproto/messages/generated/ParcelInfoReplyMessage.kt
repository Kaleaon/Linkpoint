package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelInfoReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var parcelId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)
    var desc: ByteArray = ByteArray(0)
    var actualArea: Int = 0
    var billableArea: Int = 0
    var flags: Int = 0
    var globalX: Float = 0f
    var globalY: Float = 0f
    var globalZ: Float = 0f
    var simName: ByteArray = ByteArray(0)
    var snapshotId: UUID = UUID(0L, 0L)
    var dwell: Float = 0f
    var salePrice: Int = 0
    var auctionId: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, parcelId)
        packUUID(buffer, ownerId)
        packVariable(buffer, name, 1)
        packVariable(buffer, desc, 1)
        packInt(buffer, actualArea)
        packInt(buffer, billableArea)
        packByte(buffer, flags)
        packFloat(buffer, globalX)
        packFloat(buffer, globalY)
        packFloat(buffer, globalZ)
        packVariable(buffer, simName, 1)
        packUUID(buffer, snapshotId)
        packFloat(buffer, dwell)
        packInt(buffer, salePrice)
        packInt(buffer, auctionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        parcelId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
        desc = unpackVariable(buffer, 1)
        actualArea = unpackInt(buffer)
        billableArea = unpackInt(buffer)
        flags = unpackByte(buffer)
        globalX = unpackFloat(buffer)
        globalY = unpackFloat(buffer)
        globalZ = unpackFloat(buffer)
        simName = unpackVariable(buffer, 1)
        snapshotId = unpackUUID(buffer)
        dwell = unpackFloat(buffer)
        salePrice = unpackInt(buffer)
        auctionId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0037.toInt()

    override fun getMessageName(): String = "ParcelInfoReply"
}
