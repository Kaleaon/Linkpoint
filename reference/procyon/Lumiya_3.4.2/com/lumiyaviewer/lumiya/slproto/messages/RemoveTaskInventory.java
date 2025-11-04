// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RemoveTaskInventory extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;
    
    public RemoveTaskInventory() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.InventoryData_Field = new InventoryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 56;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRemoveTaskInventory(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)31);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.InventoryData_Field.LocalID);
        this.packUUID(byteBuffer, this.InventoryData_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.LocalID = this.unpackInt(byteBuffer);
        this.InventoryData_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
        public UUID ItemID;
        public int LocalID;
    }
}
