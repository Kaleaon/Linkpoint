package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInbound : SLMessage() {
    public DataBlock DataBlock_Field = DataBlock()
    public TargetBlock TargetBlock_Field = TargetBlock()

    @JvmStatic
    class DataBlock {
        public UUID ChannelID
        public Int IntValue
        public UUID ItemID
        public Byte[] StringValue
        public UUID TaskID
    }

    @JvmStatic
    class TargetBlock {
        public Int GridX
        public Int GridY
    }

    public RpcScriptRequestInbound() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 54 + 12
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInbound(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -97)
        packInt(byteBuffer, this.TargetBlock_Field.GridX)
        packInt(byteBuffer, this.TargetBlock_Field.GridY)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID)
        packInt(byteBuffer, this.DataBlock_Field.IntValue)
        packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.GridX = unpackInt(byteBuffer)
        this.TargetBlock_Field.GridY = unpackInt(byteBuffer)
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}
