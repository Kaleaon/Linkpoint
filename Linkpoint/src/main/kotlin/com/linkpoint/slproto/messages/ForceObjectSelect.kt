package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class ForceObjectSelect : SLMessage() {
    public ArrayList<Data> Data_Fields = ArrayList<>()
    public Header Header_Field

    @JvmStatic
    class Data {
        public Int LocalID
    }

    @JvmStatic
    class Header {
        public Boolean ResetList
    }

    public ForceObjectSelect() {
        this.zeroCoded = false
        this.Header_Field = Header()
    }

    public fun CalcPayloadSize(): Int {
        return (this.Data_Fields.size() * 4) + 6
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleForceObjectSelect(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -51)
        packBoolean(byteBuffer, this.Header_Field.ResetList)
        byteBuffer.put((Byte) this.Data_Fields.size())
        for (Data data : this.Data_Fields) {
            packInt(byteBuffer, data.LocalID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Header_Field.ResetList = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val data: Data = Data()
            data.LocalID = unpackInt(byteBuffer)
            this.Data_Fields.add(data)
        }
    }
}
