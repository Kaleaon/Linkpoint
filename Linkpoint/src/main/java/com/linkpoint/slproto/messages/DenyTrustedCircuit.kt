package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DenyTrustedCircuit : SLMessage() {
    val DataBlock_Field = DataBlock()

    class DataBlock {
        var EndPointID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleDenyTrustedCircuit(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-119).toByte())
        packUUID(buffer, DataBlock_Field.EndPointID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        DataBlock_Field.EndPointID = unpackUUID(buffer)
    }
}