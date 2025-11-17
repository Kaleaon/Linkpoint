package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class UUIDNameReply : SLMessage {
    ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = ArrayList<>()

    class UUIDNameBlock {
        ByteArray FirstName
        UUID ID
        ByteArray LastName
    }

    UUIDNameReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        Int i = 5
        Iterator<T> it = this.UUIDNameBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            UUIDNameBlock uUIDNameBlock = (UUIDNameBlock) it.next()
            i = uUIDNameBlock.LastName.length + uUIDNameBlock.FirstName.length + 17 + 1 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUUIDNameReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -20)
        byteBuffer.put((Byte) this.UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
            packVariable(byteBuffer, uUIDNameBlock.FirstName, 1)
            packVariable(byteBuffer, uUIDNameBlock.LastName, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            UUIDNameBlock uUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            uUIDNameBlock.FirstName = unpackVariable(byteBuffer, 1)
            uUIDNameBlock.LastName = unpackVariable(byteBuffer, 1)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
