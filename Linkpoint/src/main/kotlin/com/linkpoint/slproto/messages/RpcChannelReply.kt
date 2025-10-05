package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelReply : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()

    @JvmStatic
    class DataBlock {
        public UUID ChannelID
        public UUID ItemID
        public UUID TaskID
    }

    public RpcChannelReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 52
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcChannelReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -98)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
    }
}
