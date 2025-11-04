// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChangeInventoryItemFlags extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields;
    
    public ChangeInventoryItemFlags() {
        this.InventoryData_Fields = new ArrayList<InventoryData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryData_Fields.size() * 20 + 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChangeInventoryItemFlags(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)15);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.InventoryData_Fields.size());
        for (final InventoryData inventoryData : this.InventoryData_Fields) {
            this.packUUID(byteBuffer, inventoryData.ItemID);
            this.packInt(byteBuffer, inventoryData.Flags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InventoryData e = new InventoryData();
            e.ItemID = this.unpackUUID(byteBuffer);
            e.Flags = this.unpackInt(byteBuffer);
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
        public int Flags;
        public UUID ItemID;
    }
}
