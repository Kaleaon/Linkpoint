// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MoveInventoryItem extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields;
    
    public MoveInventoryItem() {
        this.InventoryData_Fields = new ArrayList<InventoryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.InventoryData_Fields.iterator();
        int n = 38;
        while (iterator.hasNext()) {
            n += iterator.next().NewName.length + 33;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMoveInventoryItem(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)12);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packBoolean(byteBuffer, this.AgentData_Field.Stamp);
        byteBuffer.put((byte)this.InventoryData_Fields.size());
        for (final InventoryData inventoryData : this.InventoryData_Fields) {
            this.packUUID(byteBuffer, inventoryData.ItemID);
            this.packUUID(byteBuffer, inventoryData.FolderID);
            this.packVariable(byteBuffer, inventoryData.NewName, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.Stamp = this.unpackBoolean(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryData e = new InventoryData();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.FolderID = this.unpackUUID(byteBuffer);
            e.NewName = this.unpackVariable(byteBuffer, 1);
            this.InventoryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
        public boolean Stamp;
    }
    
    public static class InventoryData
    {
        public UUID FolderID;
        public UUID ItemID;
        public byte[] NewName;
    }
}
