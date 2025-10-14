package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ScriptDataRequest : SLMessage {
    ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    class DataBlock {
        Long Hash
        Byte[] Request
        Int RequestType
    }

    ScriptDataRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        Int i = 5
        Iterator<T> it = this.DataBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((DataBlock) it.next()).Request.length + 11 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptDataRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 81)
        byteBuffer.put((Byte) this.DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packLong(byteBuffer, dataBlock.Hash)
            packByte(byteBuffer, (Byte) dataBlock.RequestType)
            packVariable(byteBuffer, dataBlock.Request, 2)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            DataBlock dataBlock = DataBlock()
            dataBlock.Hash = unpackLong(byteBuffer)
            dataBlock.RequestType = unpackByte(byteBuffer)
            dataBlock.Request = unpackVariable(byteBuffer, 2)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
