package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RegionIDAndHandleReply : SLMessage() {
    public ReplyBlock ReplyBlock_Field = ReplyBlock()

    @JvmStatic
    class ReplyBlock {
        public Long RegionHandle
        public UUID RegionID
    }

    public RegionIDAndHandleReply() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 28
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleRegionIDAndHandleReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 54)
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID)
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
