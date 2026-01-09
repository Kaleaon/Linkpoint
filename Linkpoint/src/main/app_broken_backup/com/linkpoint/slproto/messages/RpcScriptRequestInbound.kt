package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcScriptRequestInbound : SLMessage {
    DataBlock DataBlock_Field = DataBlock()
    TargetBlock TargetBlock_Field = TargetBlock()

    class DataBlock {
        UUID ChannelID
        Int IntValue
        UUID ItemID
        ByteArray StringValue
        UUID TaskID
    }

    class TargetBlock {
        Int GridX
        Int GridY
    }

    RpcScriptRequestInbound() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.DataBlock_Field.StringValue.size + 54 + 12
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRpcScriptRequestInbound(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
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

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.TargetBlock_Field.GridX = unpackInt(byteBuffer)
        this.TargetBlock_Field.GridY = unpackInt(byteBuffer)
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}
