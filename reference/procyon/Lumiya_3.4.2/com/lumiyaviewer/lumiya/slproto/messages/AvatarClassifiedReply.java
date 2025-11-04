// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarClassifiedReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    
    public AvatarClassifiedReply() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.Data_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 17;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarClassifiedReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)42);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.TargetID);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packUUID(byteBuffer, data.ClassifiedID);
            this.packVariable(byteBuffer, data.Name, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.TargetID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.ClassifiedID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID TargetID;
    }
    
    public static class Data
    {
        public UUID ClassifiedID;
        public byte[] Name;
    }
}
