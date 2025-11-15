package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 44
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRpcChannelRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -99)
        packInt(byteBuffer, this.DataBlock_Field.GridX)
        packInt(byteBuffer, this.DataBlock_Field.GridY)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.GridX = unpackInt(byteBuffer)
        this.DataBlock_Field.GridY = unpackInt(byteBuffer)
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
    }
}
