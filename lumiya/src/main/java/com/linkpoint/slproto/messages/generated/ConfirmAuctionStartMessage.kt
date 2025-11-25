package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ConfirmAuctionStartMessage : SLMessage() {
    var parcelId: UUID = UUID(0L, 0L)
    var auctionId: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, parcelId)
        packInt(buffer, auctionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        parcelId = unpackUUID(buffer)
        auctionId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00E6.toInt()

    override fun getMessageName(): String = "ConfirmAuctionStart"
}
