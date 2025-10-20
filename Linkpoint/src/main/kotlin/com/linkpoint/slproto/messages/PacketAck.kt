package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

/**
 * PacketAck - Acknowledge received reliable packets
 */
class PacketAck(
    val packets: List<Packet>
) : SLMessage() {
    
    data class Packet(val id: Int)
    
    override fun getMessageName() = "PacketAck"
    
    override fun encode(buffer: ByteBuffer) {
        buffer.put(packets.size.toByte())
        for (packet in packets) {
            buffer.putInt(packet.id)
        }
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Decoded by MessageDecoder
    }
}
