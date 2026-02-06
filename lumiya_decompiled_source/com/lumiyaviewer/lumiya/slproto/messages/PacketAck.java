package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class PacketAck extends SLMessage {
    public ArrayList<Packets> Packets_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class Packets {
        public int ID;
    }

    public PacketAck() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.Packets_Fields.size() * 4) + 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePacketAck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) -5);
        byteBuffer.put((byte) this.Packets_Fields.size());
        for (Packets packets : this.Packets_Fields) {
            packInt(byteBuffer, packets.ID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            Packets packets = new Packets();
            packets.ID = unpackInt(byteBuffer);
            this.Packets_Fields.add(packets);
        }
    }
}
