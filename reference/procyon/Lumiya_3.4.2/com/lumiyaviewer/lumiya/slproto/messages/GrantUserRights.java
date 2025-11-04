// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GrantUserRights extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Rights> Rights_Fields;
    
    public GrantUserRights() {
        this.Rights_Fields = new ArrayList<Rights>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Rights_Fields.size() * 20 + 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGrantUserRights(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)64);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.Rights_Fields.size());
        for (final Rights rights : this.Rights_Fields) {
            this.packUUID(byteBuffer, rights.AgentRelated);
            this.packInt(byteBuffer, rights.RelatedRights);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Rights e = new Rights();
            e.AgentRelated = this.unpackUUID(byteBuffer);
            e.RelatedRights = this.unpackInt(byteBuffer);
            this.Rights_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Rights
    {
        public UUID AgentRelated;
        public int RelatedRights;
    }
}
