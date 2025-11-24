package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartAuctionMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var parcelId: UUID = UUID(0L, 0L)
    var snapshotId: UUID = UUID(0L, 0L)
    var name: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, parcelId)
        packUUID(buffer, snapshotId)
        packVariable(buffer, name, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        parcelId = unpackUUID(buffer)
        snapshotId = unpackUUID(buffer)
        name = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF00E5.toInt()

    override fun getMessageName(): String = "StartAuction"
}
