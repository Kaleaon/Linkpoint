// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UUIDNameRequest extends SLMessage
{
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields;
    
    public UUIDNameRequest() {
        this.UUIDNameBlock_Fields = new ArrayList<UUIDNameBlock>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.UUIDNameBlock_Fields.size() * 16 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUUIDNameRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-21));
        byteBuffer.put((byte)this.UUIDNameBlock_Fields.size());
        final Iterator<Object> iterator = this.UUIDNameBlock_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().ID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final UUIDNameBlock e = new UUIDNameBlock();
            e.ID = this.unpackUUID(byteBuffer);
            this.UUIDNameBlock_Fields.add(e);
        }
    }
    
    public static class UUIDNameBlock
    {
        public UUID ID;
    }
}
