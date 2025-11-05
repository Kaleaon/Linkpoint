package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

/**
 * PacketAck - Acknowledge received reliable packets
 */
class PacketAck(
    val packets: MutableList<Packet> = mutableListOf()
) : SLMessage() {

    data class Packet(val id: Int)

    init {
        isReliable = false
    }

    override fun getMessageName() = "PacketAck"

    override fun encode(buffer: ByteBuffer) {
        buffer.put(packets.size.toByte())
        for (packet in packets) {
            buffer.putInt(packet.id)
        }
    }

    override fun decode(buffer: ByteBuffer) {
        packets.clear()
        val count = buffer.get().toInt() and 0xFF
        repeat(count) {
            packets.add(Packet(buffer.getInt()))
        }
    }
}
