package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelReply : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        UUID ChannelID
        UUID ItemID
        UUID TaskID
    }

    RpcChannelReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleRpcChannelReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -98)
        packUUID(byteBuffer, this.DataBlock_Field.TaskID)
        packUUID(byteBuffer, this.DataBlock_Field.ItemID)
        packUUID(byteBuffer, this.DataBlock_Field.ChannelID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.DataBlock_Field.TaskID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ItemID = unpackUUID(byteBuffer)
        this.DataBlock_Field.ChannelID = unpackUUID(byteBuffer)
    }
}
