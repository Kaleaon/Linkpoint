// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RezObjectFromNotecard extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields;
    public NotecardData NotecardData_Field;
    public RezData RezData_Field;
    
    public RezObjectFromNotecard() {
        this.InventoryData_Fields = new ArrayList<InventoryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RezData_Field = new RezData();
        this.NotecardData_Field = new NotecardData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryData_Fields.size() * 16 + 161;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRezObjectFromNotecard(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)38);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.RezData_Field.FromTaskID);
        this.packByte(byteBuffer, (byte)this.RezData_Field.BypassRaycast);
        this.packLLVector3(byteBuffer, this.RezData_Field.RayStart);
        this.packLLVector3(byteBuffer, this.RezData_Field.RayEnd);
        this.packUUID(byteBuffer, this.RezData_Field.RayTargetID);
        this.packBoolean(byteBuffer, this.RezData_Field.RayEndIsIntersection);
        this.packBoolean(byteBuffer, this.RezData_Field.RezSelected);
        this.packBoolean(byteBuffer, this.RezData_Field.RemoveItem);
        this.packInt(byteBuffer, this.RezData_Field.ItemFlags);
        this.packInt(byteBuffer, this.RezData_Field.GroupMask);
        this.packInt(byteBuffer, this.RezData_Field.EveryoneMask);
        this.packInt(byteBuffer, this.RezData_Field.NextOwnerMask);
        this.packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID);
        this.packUUID(byteBuffer, this.NotecardData_Field.ObjectID);
        byteBuffer.put((byte)this.InventoryData_Fields.size());
        final Iterator<Object> iterator = this.InventoryData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().ItemID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.RezData_Field.FromTaskID = this.unpackUUID(byteBuffer);
        this.RezData_Field.BypassRaycast = (this.unpackByte(byteBuffer) & 0xFF);
        this.RezData_Field.RayStart = this.unpackLLVector3(byteBuffer);
        this.RezData_Field.RayEnd = this.unpackLLVector3(byteBuffer);
        this.RezData_Field.RayTargetID = this.unpackUUID(byteBuffer);
        this.RezData_Field.RayEndIsIntersection = this.unpackBoolean(byteBuffer);
        this.RezData_Field.RezSelected = this.unpackBoolean(byteBuffer);
        this.RezData_Field.RemoveItem = this.unpackBoolean(byteBuffer);
        this.RezData_Field.ItemFlags = this.unpackInt(byteBuffer);
        this.RezData_Field.GroupMask = this.unpackInt(byteBuffer);
        this.RezData_Field.EveryoneMask = this.unpackInt(byteBuffer);
        this.RezData_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.NotecardData_Field.NotecardItemID = this.unpackUUID(byteBuffer);
        this.NotecardData_Field.ObjectID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryData e = new InventoryData();
            e.ItemID = this.unpackUUID(byteBuffer);
            this.InventoryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
        public UUID ItemID;
    }
    
    public static class NotecardData
    {
        public UUID NotecardItemID;
        public UUID ObjectID;
    }
    
    public static class RezData
    {
        public int BypassRaycast;
        public int EveryoneMask;
        public UUID FromTaskID;
        public int GroupMask;
        public int ItemFlags;
        public int NextOwnerMask;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public boolean RemoveItem;
        public boolean RezSelected;
    }
}
