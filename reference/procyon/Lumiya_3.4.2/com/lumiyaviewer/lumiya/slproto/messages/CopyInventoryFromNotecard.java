// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CopyInventoryFromNotecard extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields;
    public NotecardData NotecardData_Field;
    
    public CopyInventoryFromNotecard() {
        this.InventoryData_Fields = new ArrayList<InventoryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.NotecardData_Field = new NotecardData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryData_Fields.size() * 32 + 69;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCopyInventoryFromNotecard(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)9);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID);
        this.packUUID(byteBuffer, this.NotecardData_Field.ObjectID);
        byteBuffer.put((byte)this.InventoryData_Fields.size());
        for (final InventoryData inventoryData : this.InventoryData_Fields) {
            this.packUUID(byteBuffer, inventoryData.ItemID);
            this.packUUID(byteBuffer, inventoryData.FolderID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.NotecardData_Field.NotecardItemID = this.unpackUUID(byteBuffer);
        this.NotecardData_Field.ObjectID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryData e = new InventoryData();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.FolderID = this.unpackUUID(byteBuffer);
            this.InventoryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
        public UUID FolderID;
        public UUID ItemID;
    }
    
    public static class NotecardData
    {
        public UUID NotecardItemID;
        public UUID ObjectID;
    }
}
