package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class LogTextMessage : SLMessage() {
    public ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    @JvmStatic
    class DataBlock {
        public UUID FromAgentId
        public Double GlobalX
        public Double GlobalY
        public ByteArray Message
        public Int Time
        public UUID ToAgentId
    }

    public LogTextMessage() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        Int i = 5
        Iterator<T> it = this.DataBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((DataBlock) it.next()).Message.length + 54 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogTextMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -121)
        byteBuffer.put((Byte) this.DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.FromAgentId)
            packUUID(byteBuffer, dataBlock.ToAgentId)
            packDouble(byteBuffer, dataBlock.GlobalX)
            packDouble(byteBuffer, dataBlock.GlobalY)
            packInt(byteBuffer, dataBlock.Time)
            packVariable(byteBuffer, dataBlock.Message, 2)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            DataBlock dataBlock = DataBlock()
            dataBlock.FromAgentId = unpackUUID(byteBuffer)
            dataBlock.ToAgentId = unpackUUID(byteBuffer)
            dataBlock.GlobalX = unpackDouble(byteBuffer)
            dataBlock.GlobalY = unpackDouble(byteBuffer)
            dataBlock.Time = unpackInt(byteBuffer)
            dataBlock.Message = unpackVariable(byteBuffer, 2)
            this.DataBlock_Fields.add(dataBlock)
        }
    }
}
