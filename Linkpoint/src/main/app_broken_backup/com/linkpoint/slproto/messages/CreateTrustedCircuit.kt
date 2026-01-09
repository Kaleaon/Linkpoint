package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateTrustedCircuit : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        ByteArray Digest
        UUID EndPointID
    }

    CreateTrustedCircuit() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCreateTrustedCircuit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -120)
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID)
        packFixed(byteBuffer, this.DataBlock_Field.Digest, 32)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer)
        this.DataBlock_Field.Digest = unpackFixed(byteBuffer, 32)
    }
}
