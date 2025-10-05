package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateTrustedCircuit : SLMessage() {
    val DataBlock_Field = DataBlock()

    class DataBlock {
        var EndPointID: UUID? = null
        lateinit var Digest: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleCreateTrustedCircuit(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-120).toByte())
        packUUID(buffer, DataBlock_Field.EndPointID)
        packFixed(buffer, DataBlock_Field.Digest, 32)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        DataBlock_Field.EndPointID = unpackUUID(buffer)
        DataBlock_Field.Digest = unpackFixed(buffer, 32)
    }
}