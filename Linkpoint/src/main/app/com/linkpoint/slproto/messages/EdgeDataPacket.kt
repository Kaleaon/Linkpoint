package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EdgeDataPacket : SLMessage {
    EdgeData EdgeData_Field = EdgeData()

    class EdgeData {
        Int Direction
        ByteArray LayerData
        Int LayerType
    }

    EdgeDataPacket() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.EdgeData_Field.LayerData.size + 4 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEdgeDataPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.put(Ascii.CAN)
        packByte(byteBuffer, (this as byte).EdgeData_Field.LayerType)
        packByte(byteBuffer, (this as byte).EdgeData_Field.Direction)
        packVariable(byteBuffer, this.EdgeData_Field.LayerData, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.EdgeData_Field.LayerType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.Direction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.LayerData = unpackVariable(byteBuffer, 2)
    }
}
