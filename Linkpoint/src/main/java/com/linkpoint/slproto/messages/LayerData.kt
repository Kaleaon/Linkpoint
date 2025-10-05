package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class LayerData : SLMessage() {
    val LayerID_Field = LayerID()
    val LayerDataData_Field = LayerDataData()

    class LayerID {
        var Type: Int = 0
    }

    class LayerDataData {
        lateinit var Data: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return LayerDataData_Field.Data.size + 2 + 2
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleLayerData(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(Ascii.VT)
        packByte(buffer, LayerID_Field.Type.toByte())
        packVariable(buffer, LayerDataData_Field.Data, 2)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        LayerID_Field.Type = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
        LayerDataData_Field.Data = unpackVariable(buffer, 2)
    }
}