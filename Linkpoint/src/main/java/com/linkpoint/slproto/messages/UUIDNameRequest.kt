package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UUIDNameRequest : SLMessage() {
    val UUIDNameBlock_Fields = ArrayList<UUIDNameBlock>()

    class UUIDNameBlock {
        var ID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = (UUIDNameBlock_Fields.size * 16) + 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleUUIDNameRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-21).toByte())
        buffer.put(UUIDNameBlock_Fields.size.toByte())
        for (block in UUIDNameBlock_Fields) {
            packUUID(buffer, block.ID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val block = UUIDNameBlock()
            block.ID = unpackUUID(buffer)
            UUIDNameBlock_Fields.add(block)
        }
    }
}