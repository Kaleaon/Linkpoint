// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupNoticesListReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<Data> Data_Fields;
    
    public GroupNoticesListReply() {
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
            n += data.Subject.length + (data.FromName.length + 22 + 2) + 1 + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupNoticesListReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)59);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        byteBuffer.put((byte)this.Data_Fields.size());
        for (final Data data : this.Data_Fields) {
            this.packUUID(byteBuffer, data.NoticeID);
            this.packInt(byteBuffer, data.Timestamp);
            this.packVariable(byteBuffer, data.FromName, 2);
            this.packVariable(byteBuffer, data.Subject, 2);
            this.packBoolean(byteBuffer, data.HasAttachment);
            this.packByte(byteBuffer, (byte)data.AssetType);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Data e = new Data();
            e.NoticeID = this.unpackUUID(byteBuffer);
            e.Timestamp = this.unpackInt(byteBuffer);
            e.FromName = this.unpackVariable(byteBuffer, 2);
            e.Subject = this.unpackVariable(byteBuffer, 2);
            e.HasAttachment = this.unpackBoolean(byteBuffer);
            e.AssetType = (this.unpackByte(byteBuffer) & 0xFF);
            this.Data_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
    }
    
    public static class Data
    {
        public int AssetType;
        public byte[] FromName;
        public boolean HasAttachment;
        public UUID NoticeID;
        public byte[] Subject;
        public int Timestamp;
    }
}
