// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectPermissions extends SLMessage
{
    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public ObjectPermissions() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.HeaderData_Field = new HeaderData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 10 + 38;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectPermissions(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)105);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packBoolean(byteBuffer, this.HeaderData_Field.Override);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packInt(byteBuffer, objectData.ObjectLocalID);
            this.packByte(byteBuffer, (byte)objectData.Field);
            this.packByte(byteBuffer, (byte)objectData.Set);
            this.packInt(byteBuffer, objectData.Mask);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.HeaderData_Field.Override = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ObjectLocalID = this.unpackInt(byteBuffer);
            e.Field = (this.unpackByte(byteBuffer) & 0xFF);
            e.Set = (this.unpackByte(byteBuffer) & 0xFF);
            e.Mask = this.unpackInt(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class HeaderData
    {
        public boolean Override;
    }
    
    public static class ObjectData
    {
        public int Field;
        public int Mask;
        public int ObjectLocalID;
        public int Set;
    }
}
