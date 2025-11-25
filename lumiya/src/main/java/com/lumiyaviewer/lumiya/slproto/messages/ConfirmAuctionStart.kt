package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class ConfirmAuctionStart : SLMessage {
    var AuctionData_Field: AuctionData = AuctionData()

    class AuctionData {
        var AuctionID: Int = 0
        var ParcelID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 24
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleConfirmAuctionStart(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-26).toByte())
        packUUID(byteBuffer, this.AuctionData_Field.ParcelID)
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AuctionData_Field.ParcelID = unpackUUID(byteBuffer)
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
