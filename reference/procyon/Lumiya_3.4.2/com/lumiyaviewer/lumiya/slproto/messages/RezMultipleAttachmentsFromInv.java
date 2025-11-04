// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RezMultipleAttachmentsFromInv extends SLMessage
{
    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public RezMultipleAttachmentsFromInv() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.HeaderData_Field = new HeaderData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.ObjectData_Fields.iterator();
        int n = 55;
        while (iterator.hasNext()) {
            final ObjectData objectData = iterator.next();
            n += objectData.Description.length + (objectData.Name.length + 50 + 1);
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRezMultipleAttachmentsFromInv(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-116));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.HeaderData_Field.CompoundMsgID);
        this.packByte(byteBuffer, (byte)this.HeaderData_Field.TotalObjects);
        this.packBoolean(byteBuffer, this.HeaderData_Field.FirstDetachAll);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packUUID(byteBuffer, objectData.ItemID);
            this.packUUID(byteBuffer, objectData.OwnerID);
            this.packByte(byteBuffer, (byte)objectData.AttachmentPt);
            this.packInt(byteBuffer, objectData.ItemFlags);
            this.packInt(byteBuffer, objectData.GroupMask);
            this.packInt(byteBuffer, objectData.EveryoneMask);
            this.packInt(byteBuffer, objectData.NextOwnerMask);
            this.packVariable(byteBuffer, objectData.Name, 1);
            this.packVariable(byteBuffer, objectData.Description, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.HeaderData_Field.CompoundMsgID = this.unpackUUID(byteBuffer);
        this.HeaderData_Field.TotalObjects = (this.unpackByte(byteBuffer) & 0xFF);
        this.HeaderData_Field.FirstDetachAll = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.AttachmentPt = (this.unpackByte(byteBuffer) & 0xFF);
            e.ItemFlags = this.unpackInt(byteBuffer);
            e.GroupMask = this.unpackInt(byteBuffer);
            e.EveryoneMask = this.unpackInt(byteBuffer);
            e.NextOwnerMask = this.unpackInt(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Description = this.unpackVariable(byteBuffer, 1);
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
        public UUID CompoundMsgID;
        public boolean FirstDetachAll;
        public int TotalObjects;
    }
    
    public static class ObjectData
    {
        public int AttachmentPt;
        public byte[] Description;
        public int EveryoneMask;
        public int GroupMask;
        public int ItemFlags;
        public UUID ItemID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID OwnerID;
    }
}
