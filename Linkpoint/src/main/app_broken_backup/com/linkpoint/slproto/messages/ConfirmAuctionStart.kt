package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ConfirmAuctionStart : SLMessage {
    AuctionData AuctionData_Field = AuctionData()

    class AuctionData {
        Int AuctionID
        UUID ParcelID
    }

    ConfirmAuctionStart() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleConfirmAuctionStart(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -26)
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID)
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer)
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
