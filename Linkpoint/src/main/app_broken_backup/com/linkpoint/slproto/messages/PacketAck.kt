package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.SLMessageHandler
import java.nio.ByteBuffer

/**
 * PacketAck message - acknowledges receipt of reliable packets.
 */
class PacketAck(
    val packets: MutableList<Packet> = mutableListOf(),
) : SLMessage() {

    data class Packet(var id: Int)

    init {
        zeroCoded = false
        isReliable = false
    }

    override fun CalcPayloadSize(): Int = packets.size * 4 + 5

    override fun handleMessage(handler: SLMessageHandler) {
        handler.HandlePacketAck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort((-1).toShort())
        buffer.put((-1).toByte())
        buffer.put((-5).toByte())
        buffer.put(packets.size.toByte())
        for (packet in packets) {
            packInt(buffer, packet.id)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        val count = buffer.get().toInt() and 0xFF
        repeat(count) {
            packets.add(Packet(unpackInt(buffer)))
        }
    }
}
