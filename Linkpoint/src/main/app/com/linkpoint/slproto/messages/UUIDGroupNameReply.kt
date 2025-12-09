package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class UUIDGroupNameReply : SLMessage {
    ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = ArrayList<>()

    class UUIDNameBlock {
        ByteArray GroupName
        UUID ID
    }

    UUIDGroupNameReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        Int i = 5
        Iterator<T> it = this.UUIDNameBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as UUIDNameBlock).next()).GroupName.size + 17 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleUUIDGroupNameReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -18)
        byteBuffer.put((this as Byte).UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
            packVariable(byteBuffer, uUIDNameBlock.GroupName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            UUIDNameBlock uUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            uUIDNameBlock.GroupName = unpackVariable(byteBuffer, 1)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
