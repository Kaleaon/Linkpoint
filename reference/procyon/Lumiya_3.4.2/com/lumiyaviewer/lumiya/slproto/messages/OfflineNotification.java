// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class OfflineNotification extends SLMessage
{
    public ArrayList<AgentBlock> AgentBlock_Fields;
    
    public OfflineNotification() {
        this.AgentBlock_Fields = new ArrayList<AgentBlock>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentBlock_Fields.size() * 16 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleOfflineNotification(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)67);
        byteBuffer.put((byte)this.AgentBlock_Fields.size());
        final Iterator<Object> iterator = this.AgentBlock_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().AgentID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AgentBlock e = new AgentBlock();
            e.AgentID = this.unpackUUID(byteBuffer);
            this.AgentBlock_Fields.add(e);
        }
    }
    
    public static class AgentBlock
    {
        public UUID AgentID;
    }
}
