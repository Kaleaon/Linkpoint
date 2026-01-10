package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class LogTextMessage : SLMessage {
    ArrayList<DataBlock> DataBlock_Fields = ArrayList<>()

    class DataBlock {
        UUID FromAgentId
        Double GlobalX
        Double GlobalY
        ByteArray Message
        Int Time
        UUID ToAgentId
    }

    constructor() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 5
        Iterator<T> it = this.DataBlock_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as DataBlock).next()).Message.size + 54 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler)  {
        sLMessageHandler.HandleLogTextMessage(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -121)
        byteBuffer.put((this as Byte).DataBlock_Fields.size())
        for (DataBlock dataBlock : this.DataBlock_Fields) {
            packUUID(byteBuffer, dataBlock.FromAgentId)
            packUUID(byteBuffer, dataBlock.ToAgentId)
            packDouble(byteBuffer, dataBlock.GlobalX)
            packDouble(byteBuffer, dataBlock.GlobalY)
            packInt(byteBuffer, dataBlock.Time)
            packVariable(byteBuffer, dataBlock.Message, 2)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
