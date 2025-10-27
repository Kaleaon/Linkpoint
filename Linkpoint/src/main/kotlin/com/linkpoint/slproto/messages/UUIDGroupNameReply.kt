package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class UUIDGroupNameReply : SLMessage() {
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = ArrayList<>()

    @JvmStatic
    class UUIDNameBlock {
        public ByteArray GroupName
        public UUID ID
    }

    public UUIDGroupNameReply() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 5
        val it: Iterator<T> = this.UUIDNameBlock_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((UUIDNameBlock) it.next()).GroupName.length + 17 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleUUIDGroupNameReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -18)
        byteBuffer.put((Byte) this.UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
            packVariable(byteBuffer, uUIDNameBlock.GroupName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val uUIDNameBlock: UUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            uUIDNameBlock.GroupName = unpackVariable(byteBuffer, 1)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
