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

    Int CalcPayloadSize() {
        Int i = 5
        Iterator<T> it = this.UUIDNameBlock_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((UUIDNameBlock) it.next()).GroupName.length + 17 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUUIDGroupNameReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -18)
        byteBuffer.put((Byte) this.UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
            packVariable(byteBuffer, uUIDNameBlock.GroupName, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            UUIDNameBlock uUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            uUIDNameBlock.GroupName = unpackVariable(byteBuffer, 1)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
