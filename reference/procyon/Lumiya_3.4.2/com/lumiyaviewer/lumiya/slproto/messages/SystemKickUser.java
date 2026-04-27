// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SystemKickUser extends SLMessage
{
    public ArrayList<AgentInfo> AgentInfo_Fields;
    
    public SystemKickUser() {
        this.AgentInfo_Fields = new ArrayList<AgentInfo>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentInfo_Fields.size() * 16 + 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSystemKickUser(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-90));
        byteBuffer.put((byte)this.AgentInfo_Fields.size());
        final Iterator<Object> iterator = this.AgentInfo_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().AgentID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AgentInfo e = new AgentInfo();
            e.AgentID = this.unpackUUID(byteBuffer);
            this.AgentInfo_Fields.add(e);
        }
    }
    
    public static class AgentInfo
    {
        public UUID AgentID;
    }
}
