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

    public fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleConfirmAuctionStart(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -26)
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID)
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer)
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
