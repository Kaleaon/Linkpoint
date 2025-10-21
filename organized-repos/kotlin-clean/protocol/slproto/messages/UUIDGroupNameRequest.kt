package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class UUIDGroupNameRequest : SLMessage() {
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = ArrayList<>()

    @JvmStatic
    class UUIDNameBlock {
        public UUID ID
    }

    public UUIDGroupNameRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return (this.UUIDNameBlock_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUUIDGroupNameRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -19)
        byteBuffer.put((Byte) this.UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            UUIDNameBlock uUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
