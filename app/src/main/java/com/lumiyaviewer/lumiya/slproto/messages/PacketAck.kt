package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList

class PacketAck : SLMessage {
    ArrayList<Packets> Packets_Fields = ArrayList<>()

    class Packets {
        Int ID
    }

    PacketAck() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return (this.Packets_Fields.size() * 4) + 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePacketAck(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) -5)
        byteBuffer.put((Byte) this.Packets_Fields.size())
        for (Packets packets : this.Packets_Fields) {
            packInt(byteBuffer, packets.ID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Packets packets = Packets()
            packets.ID = unpackInt(byteBuffer)
            this.Packets_Fields.add(packets)
        }
    }
}
