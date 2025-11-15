package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class EdgeDataPacket : SLMessage {
    EdgeData EdgeData_Field = EdgeData()

    class EdgeData {
        Int Direction
        byte[] LayerData
        Int LayerType
    }

    EdgeDataPacket() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.EdgeData_Field.LayerData.length + 4 + 1
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEdgeDataPacket(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.CAN)
        packByte(byteBuffer, (byte) this.EdgeData_Field.LayerType)
        packByte(byteBuffer, (byte) this.EdgeData_Field.Direction)
        packVariable(byteBuffer, this.EdgeData_Field.LayerData, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.EdgeData_Field.LayerType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.Direction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.LayerData = unpackVariable(byteBuffer, 2)
    }
}
