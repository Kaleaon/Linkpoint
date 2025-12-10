package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 28
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRegionIDAndHandleReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 54)
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID)
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
