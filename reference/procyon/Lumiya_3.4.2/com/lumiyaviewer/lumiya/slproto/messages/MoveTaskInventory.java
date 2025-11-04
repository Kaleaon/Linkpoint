// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class MoveTaskInventory extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;
    
    public MoveTaskInventory() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.InventoryData_Field = new InventoryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleMoveTaskInventory(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)32);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.FolderID);
        this.packInt(byteBuffer, this.InventoryData_Field.LocalID);
        this.packUUID(byteBuffer, this.InventoryData_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.LocalID = this.unpackInt(byteBuffer);
        this.InventoryData_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID FolderID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
        public UUID ItemID;
        public int LocalID;
    }
}
