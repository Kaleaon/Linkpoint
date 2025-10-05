package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionHandleRequest : SLMessage() {
    val RequestBlock_Field = RequestBlock()

    class RequestBlock {
        var RegionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRegionHandleRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(53.toByte())
        packUUID(buffer, RequestBlock_Field.RegionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        RequestBlock_Field.RegionID = unpackUUID(buffer)
    }
}