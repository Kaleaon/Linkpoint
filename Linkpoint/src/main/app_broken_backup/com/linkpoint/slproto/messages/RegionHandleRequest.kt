package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionHandleRequest : SLMessage {
    RequestBlock RequestBlock_Field = RequestBlock()

    class RequestBlock {
        UUID RegionID
    }

    RegionHandleRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRegionHandleRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 53)
        packUUID(byteBuffer, this.RequestBlock_Field.RegionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.RequestBlock_Field.RegionID = unpackUUID(byteBuffer)
    }
}
