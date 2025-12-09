package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DenyTrustedCircuit : SLMessage {
    DataBlock DataBlock_Field = DataBlock()

    class DataBlock {
        UUID EndPointID
    }

    DenyTrustedCircuit() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDenyTrustedCircuit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -119)
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer)
    }
}
