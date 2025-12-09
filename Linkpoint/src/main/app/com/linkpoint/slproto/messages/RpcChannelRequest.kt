package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelRequest : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        Int GridX
        Int GridY
        UUID ItemID
        UUID TaskID
    }

    RpcChannelRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 44
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleRpcChannelRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -99)
        packInt(byteBuffer, this.DataBlock_Field.GridX)
        packInt(byteBuffer, this.DataBlock_Field.GridY)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.GridX = unpackInt(byteBuffer)
        this.DataBlock_Field.GridY = unpackInt(byteBuffer)
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
    }
}
