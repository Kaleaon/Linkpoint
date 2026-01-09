package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class LayerData : SLMessage {
    LayerDataData LayerDataData_Field = LayerDataData()
    LayerID LayerID_Field = LayerID()

    class LayerDataData {
        ByteArray Data
    }

    class LayerID {
        Int Type
    }

    LayerData() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.LayerDataData_Field.Data.size + 2 + 2
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleLayerData(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put(Ascii.VT)
        packByte(byteBuffer, (this as byte).LayerID_Field.Type)
        packVariable(byteBuffer, this.LayerDataData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.LayerID_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.LayerDataData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
