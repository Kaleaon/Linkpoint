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

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandleRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 53)
        packUUID(byteBuffer, this.RequestBlock_Field.RegionID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestBlock_Field.RegionID = unpackUUID(byteBuffer)
    }
}
