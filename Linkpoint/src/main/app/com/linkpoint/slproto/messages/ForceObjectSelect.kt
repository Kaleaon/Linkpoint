package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ForceObjectSelect : SLMessage {
    ArrayList<Data> Data_Fields = ArrayList<>()
    Header Header_Field

    class Data {
        Int LocalID
    }

    class Header {
        Boolean ResetList
    }

    ForceObjectSelect() {
        this.zeroCoded = false
        this.Header_Field = Header()
    }

    Int CalcPayloadSize() {
        return (this.Data_Fields.size() * 4) + 6
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleForceObjectSelect(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -51)
        packBoolean(byteBuffer, this.Header_Field.ResetList)
        byteBuffer.put((byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.LocalID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Header_Field.ResetList = unpackBoolean(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Data data = Data()
            data.LocalID = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
