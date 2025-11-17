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

    Int CalcPayloadSize() {
        return 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleConfirmAuctionStart(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -26)
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID)
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer)
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
