// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class PacketAck extends SLMessage
{
    public ArrayList<Packets> Packets_Fields;
    
    public PacketAck() {
        this.Packets_Fields = new ArrayList<Packets>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Packets_Fields.size() * 4 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandlePacketAck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)(-5));
        byteBuffer.put((byte)this.Packets_Fields.size());
        final Iterator<Object> iterator = this.Packets_Fields.iterator();
        while (iterator.hasNext()) {
            this.packInt(byteBuffer, iterator.next().ID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Packets e = new Packets();
            e.ID = this.unpackInt(byteBuffer);
            this.Packets_Fields.add(e);
        }
    }
    
    public static class Packets
    {
        public int ID;
    }
}
