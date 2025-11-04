// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateLandmarkForEvent extends SLMessage
{
    public AgentData AgentData_Field;
    public EventData EventData_Field;
    public InventoryBlock InventoryBlock_Field;
    
    public CreateLandmarkForEvent() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.EventData_Field = new EventData();
        this.InventoryBlock_Field = new InventoryBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 17 + 40;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateLandmarkForEvent(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)50);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.EventData_Field.EventID);
        this.packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        this.packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.EventData_Field.EventID = this.unpackInt(byteBuffer);
        this.InventoryBlock_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryBlock_Field.Name = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class EventData
    {
        public int EventID;
    }
    
    public static class InventoryBlock
    {
        public UUID FolderID;
        public byte[] Name;
    }
}
