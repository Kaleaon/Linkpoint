package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class LayerData : SLMessage {
    LayerDataData LayerDataData_Field = LayerDataData()
    LayerID LayerID_Field = LayerID()

    class LayerDataData {
        byte[] Data
    }

    class LayerID {
        Int Type
    }

    LayerData() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.LayerDataData_Field.Data.length + 2 + 2
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLayerData(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.VT)
        packByte(byteBuffer, (byte) this.LayerID_Field.Type)
        packVariable(byteBuffer, this.LayerDataData_Field.Data, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.LayerID_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.LayerDataData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
