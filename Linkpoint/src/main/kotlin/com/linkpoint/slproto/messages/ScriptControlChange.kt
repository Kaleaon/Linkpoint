package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ScriptControlChange : SLMessage() {
    public ArrayList<Data> Data_Fields = ArrayList<>()

    @JvmStatic
    class Data {
        public Int Controls
        public Boolean PassToAgent
        public Boolean TakeControls
    }

    public ScriptControlChange() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 6) + 5
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleScriptControlChange(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -67)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packBoolean(byteBuffer, data.TakeControls)
            packInt(byteBuffer, data.Controls)
            packBoolean(byteBuffer, data.PassToAgent)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val data: Data = Data()
            data.TakeControls = unpackBoolean(byteBuffer)
            data.Controls = unpackInt(byteBuffer)
            data.PassToAgent = unpackBoolean(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
