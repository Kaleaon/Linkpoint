package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class UUIDNameRequest : SLMessage {
    ArrayList<UUIDNameBlock> UUIDNameBlock_Fields = ArrayList<>()

    class UUIDNameBlock {
        UUID ID
    }

    UUIDNameRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return (this.UUIDNameBlock_Fields.size() * 16) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleUUIDNameRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -21)
        byteBuffer.put((this as Byte).UUIDNameBlock_Fields.size())
        for (UUIDNameBlock uUIDNameBlock : this.UUIDNameBlock_Fields) {
            packUUID(byteBuffer, uUIDNameBlock.ID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            UUIDNameBlock uUIDNameBlock = UUIDNameBlock()
            uUIDNameBlock.ID = unpackUUID(byteBuffer)
            this.UUIDNameBlock_Fields.add(uUIDNameBlock)
        }
    }
}
