package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionIDAndHandleReply : SLMessage {
    ReplyBlock ReplyBlock_Field = ReplyBlock()

    class ReplyBlock {
        Long RegionHandle
        UUID RegionID
    }

    RegionIDAndHandleReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 28
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionIDAndHandleReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 54)
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID)
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
