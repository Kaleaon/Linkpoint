package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EdgeDataPacket : SLMessage() {
    public EdgeData EdgeData_Field = EdgeData()

    @JvmStatic
    class EdgeData {
        public Int Direction
        public Byte[] LayerData
        public Int LayerType
    }

    public EdgeDataPacket() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.EdgeData_Field.LayerData.length + 4 + 1
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEdgeDataPacket(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.CAN)
        packByte(byteBuffer, (Byte) this.EdgeData_Field.LayerType)
        packByte(byteBuffer, (Byte) this.EdgeData_Field.Direction)
        packVariable(byteBuffer, this.EdgeData_Field.LayerData, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.EdgeData_Field.LayerType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.Direction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.EdgeData_Field.LayerData = unpackVariable(byteBuffer, 2)
    }
}
