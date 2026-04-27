// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RezSingleAttachmentFromInv extends SLMessage
{
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    
    public RezSingleAttachmentFromInv() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Field.Name.length + 50 + 1 + this.ObjectData_Field.Description.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRezSingleAttachmentFromInv(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-117));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.ObjectData_Field.ItemID);
        this.packUUID(byteBuffer, this.ObjectData_Field.OwnerID);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.AttachmentPt);
        this.packInt(byteBuffer, this.ObjectData_Field.ItemFlags);
        this.packInt(byteBuffer, this.ObjectData_Field.GroupMask);
        this.packInt(byteBuffer, this.ObjectData_Field.EveryoneMask);
        this.packInt(byteBuffer, this.ObjectData_Field.NextOwnerMask);
        this.packVariable(byteBuffer, this.ObjectData_Field.Name, 1);
        this.packVariable(byteBuffer, this.ObjectData_Field.Description, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.ItemID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.AttachmentPt = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.ItemFlags = this.unpackInt(byteBuffer);
        this.ObjectData_Field.GroupMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.EveryoneMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ObjectData_Field.Description = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
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
