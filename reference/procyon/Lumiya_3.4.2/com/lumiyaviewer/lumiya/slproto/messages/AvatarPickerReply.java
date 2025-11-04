// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarPickerReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    
    public AvatarPickerReply() {
        this.Data_Fields = new ArrayList<Data>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.Data_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            final Data data = iterator.next();
            n += data.LastName.length + (data.FirstName.length + 17 + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarPickerReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)28);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.QueryID);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packUUID(byteBuffer, data.AvatarID);
            this.packVariable(byteBuffer, data.FirstName, 1);
            this.packVariable(byteBuffer, data.LastName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.AvatarID = this.unpackUUID(byteBuffer);
            e.FirstName = this.unpackVariable(byteBuffer, 1);
            e.LastName = this.unpackVariable(byteBuffer, 1);
            this.Data_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID QueryID;
    }
    
    public static class Data
    {
        public UUID AvatarID;
        public byte[] FirstName;
        public byte[] LastName;
    }
}
