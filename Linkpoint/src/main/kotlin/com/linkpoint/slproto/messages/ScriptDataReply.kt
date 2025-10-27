package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class ScriptDataReply : SLMessage() {
    public ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    @JvmStatic
    class DataBlock {
        public Long Hash
        public ByteArray Reply
    }

    public ScriptDataReply() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 5
        val it: Iterator<T> = this.DataBlock_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((DataBlock) it.next()).Reply.length + 10 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleScriptDataReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 82)
        byteBuffer.put((Byte) this.DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packLong(byteBuffer, dataBlock.Hash)
            packVariable(byteBuffer, dataBlock.Reply, 2)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val dataBlock: DataBlock = DataBlock()
            dataBlock.Hash = unpackLong(byteBuffer)
            dataBlock.Reply = unpackVariable(byteBuffer, 2)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
