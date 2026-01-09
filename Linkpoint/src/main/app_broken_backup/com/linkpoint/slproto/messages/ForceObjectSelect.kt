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

    fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 4) + 6
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleForceObjectSelect(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -51)
        packBoolean(byteBuffer, this.Header_Field.ResetList)
        byteBuffer.put((this as byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.LocalID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Header_Field.ResetList = unpackBoolean(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.LocalID = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
