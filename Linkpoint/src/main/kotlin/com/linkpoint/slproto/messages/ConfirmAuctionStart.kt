package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ConfirmAuctionStart : SLMessage() {
    public AuctionData AuctionData_Field = AuctionData()

    @JvmStatic
    class AuctionData {
        public Int AuctionID
        public UUID ParcelID
    }

    public ConfirmAuctionStart() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 24
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleConfirmAuctionStart(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -26)
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID)
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer)
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
