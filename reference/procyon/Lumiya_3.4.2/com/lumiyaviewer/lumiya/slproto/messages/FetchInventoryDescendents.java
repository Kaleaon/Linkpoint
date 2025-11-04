// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class FetchInventoryDescendents extends SLMessage
{
    public AgentData AgentData_Field;
    public InventoryData InventoryData_Field;
    
    public FetchInventoryDescendents() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.InventoryData_Field = new InventoryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 74;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleFetchInventoryDescendents(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)21);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.InventoryData_Field.FolderID);
        this.packUUID(byteBuffer, this.InventoryData_Field.OwnerID);
        this.packInt(byteBuffer, this.InventoryData_Field.SortOrder);
        this.packBoolean(byteBuffer, this.InventoryData_Field.FetchFolders);
        this.packBoolean(byteBuffer, this.InventoryData_Field.FetchItems);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.FolderID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.InventoryData_Field.SortOrder = this.unpackInt(byteBuffer);
        this.InventoryData_Field.FetchFolders = this.unpackBoolean(byteBuffer);
        this.InventoryData_Field.FetchItems = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class InventoryData
    {
        public boolean FetchFolders;
        public boolean FetchItems;
        public UUID FolderID;
        public UUID OwnerID;
        public int SortOrder;
    }
}
