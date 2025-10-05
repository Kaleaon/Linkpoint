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

    public Int CalcPayloadSize() {
        return 28
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionIDAndHandleReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 54)
        packUUID(byteBuffer, this.ReplyBlock_Field.RegionID)
        packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.ReplyBlock_Field.RegionHandle = unpackLong(byteBuffer)
    }
}
