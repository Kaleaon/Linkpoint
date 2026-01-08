package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CompleteAuction : SLMessage {
    var AuctionData_Field: AuctionData = AuctionData()

    class AuctionData {
        var AuctionID: Int = 0
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 4
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCompleteAuction(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-27).toByte())
        packInt(byteBuffer, this.AuctionData_Field.AuctionID)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AuctionData_Field.AuctionID = unpackInt(byteBuffer)
    }
}
