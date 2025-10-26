package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return this.DataBlock_Field.StringValue.length + 54 + 12
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcScriptRequestInbound(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.GridX = unpackInt(byteBuffer)
        this.TargetBlock_Field.GridY = unpackInt(byteBuffer)
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
        this.DataBlock_Field.IntValue = unpackInt(byteBuffer)
        this.DataBlock_Field.StringValue = unpackVariable(byteBuffer, 2)
    }
}
