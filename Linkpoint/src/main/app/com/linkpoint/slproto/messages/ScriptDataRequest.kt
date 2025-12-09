package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ScriptDataRequest : SLMessage {
    ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    class DataBlock {
        Long Hash
        ByteArray Request
        Int RequestType
    }

    ScriptDataRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        Int i = 5
        Iterator<T> it = this.DataBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as DataBlock).next()).Request.size + 11 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptDataRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 81)
        byteBuffer.put((this as Byte).DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packLong(byteBuffer, dataBlock.Hash)
            packByte(byteBuffer, (dataBlock as Byte).RequestType)
            packVariable(byteBuffer, dataBlock.Request, 2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            DataBlock dataBlock = DataBlock()
            dataBlock.Hash = unpackLong(byteBuffer)
            dataBlock.RequestType = unpackByte(byteBuffer)
            dataBlock.Request = unpackVariable(byteBuffer, 2)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
