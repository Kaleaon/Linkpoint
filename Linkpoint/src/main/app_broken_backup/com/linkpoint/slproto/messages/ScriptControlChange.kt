package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ScriptControlChange : SLMessage {
    ArrayList<Data> Data_Fields = ArrayList<>()

    class Data {
        Int Controls
        Boolean PassToAgent
        Boolean TakeControls
    }

    ScriptControlChange() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 6) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptControlChange(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -67)
        byteBuffer.put((this as Byte).Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packBoolean(byteBuffer, data.TakeControls)
            packInt(byteBuffer, data.Controls)
            packBoolean(byteBuffer, data.PassToAgent)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Data data = Data()
            data.TakeControls = unpackBoolean(byteBuffer)
            data.Controls = unpackInt(byteBuffer)
            data.PassToAgent = unpackBoolean(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
