package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class PacketAck : SLMessage() {
    val Packets_Fields = ArrayList<Packets>()

    class Packets {
        var ID: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (Packets_Fields.size * 4) + 5
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandlePacketAck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put((-1).toByte())
        buffer.put((-5).toByte())
        buffer.put(Packets_Fields.size.toByte())
        for (packets in Packets_Fields) {
            packInt(buffer, packets.ID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and UnsignedBytes.MAX_VALUE.toInt()
        for (i in 0 until count) {
            val packets = Packets()
            packets.ID = unpackInt(buffer)
            Packets_Fields.add(packets)
        }
    }
}