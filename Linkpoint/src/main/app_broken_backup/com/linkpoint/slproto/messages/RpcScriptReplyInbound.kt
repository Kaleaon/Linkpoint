package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptReplyInbound : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        UUID ChannelID
        Int IntValue
        UUID ItemID
        ByteArray StringValue
        UUID TaskID
    }

    RpcScriptReplyInbound() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.StringValue.size + 54 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRpcScriptReplyInbound(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -95)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID)
        packInt(byteBuffer, this.DataBlock_Field.IntValue)
        packVariable(byteBuffer, this.DataBlock_Field.StringValue, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}
