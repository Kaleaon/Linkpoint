package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PacketAck extends SLMessage {
    public ArrayList<Packets> Packets_Fields = new ArrayList<>();

    public static class Packets {
        public int ID;
    }

    public PacketAck() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.Packets_Fields.size() * 4) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePacketAck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) -5);
        byteBuffer.put((byte) this.Packets_Fields.size());
        for (Packets packets : this.Packets_Fields) {
            packInt(byteBuffer, packets.ID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Packets packets = new Packets();
            packets.ID = unpackInt(byteBuffer);
            this.Packets_Fields.add(packets);
        }
    }
}
