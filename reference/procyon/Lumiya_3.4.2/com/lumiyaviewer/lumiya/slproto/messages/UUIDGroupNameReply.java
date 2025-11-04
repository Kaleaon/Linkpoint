// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UUIDGroupNameReply extends SLMessage
{
    public ArrayList<UUIDNameBlock> UUIDNameBlock_Fields;
    
    public UUIDGroupNameReply() {
        this.UUIDNameBlock_Fields = new ArrayList<UUIDNameBlock>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.UUIDNameBlock_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().GroupName.length + 17;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUUIDGroupNameReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-18));
        byteBuffer.put((byte)this.UUIDNameBlock_Fields.size());
        for (final UUIDNameBlock uuidNameBlock : this.UUIDNameBlock_Fields) {
            this.packUUID(byteBuffer, uuidNameBlock.ID);
            this.packVariable(byteBuffer, uuidNameBlock.GroupName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final UUIDNameBlock e = new UUIDNameBlock();
            e.ID = this.unpackUUID(byteBuffer);
            e.GroupName = this.unpackVariable(byteBuffer, 1);
            this.UUIDNameBlock_Fields.add(e);
        }
    }
    
    public static class UUIDNameBlock
    {
        public byte[] GroupName;
        public UUID ID;
    }
}
