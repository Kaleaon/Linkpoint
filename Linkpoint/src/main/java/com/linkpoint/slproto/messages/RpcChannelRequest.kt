package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RpcChannelRequest : SLMessage() {
    val DataBlock_Field = DataBlock()

    class DataBlock {
        var GridX: Int = 0
        var GridY: Int = 0
        var TaskID: UUID? = null
        var ItemID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 44

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleRpcChannelRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-99).toByte())
        packInt(buffer, DataBlock_Field.GridX)
        packInt(buffer, DataBlock_Field.GridY)
        packUUID(buffer, DataBlock_Field.TaskID)
        packUUID(buffer, DataBlock_Field.ItemID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        DataBlock_Field.GridX = unpackInt(buffer)
        DataBlock_Field.GridY = unpackInt(buffer)
        DataBlock_Field.TaskID = unpackUUID(buffer)
        DataBlock_Field.ItemID = unpackUUID(buffer)
    }
}