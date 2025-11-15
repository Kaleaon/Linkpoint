package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDenyTrustedCircuit(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -119)
        packUUID(byteBuffer, this.DataBlock_Field.EndPointID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.EndPointID = unpackUUID(byteBuffer)
    }
}
