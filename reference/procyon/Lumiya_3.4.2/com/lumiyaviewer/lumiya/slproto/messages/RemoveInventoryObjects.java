// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RemoveInventoryObjects extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields;
    public ArrayList<ItemData> ItemData_Fields;
    
    public RemoveInventoryObjects() {
        this.FolderData_Fields = new ArrayList<FolderData>();
        this.ItemData_Fields = new ArrayList<ItemData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.FolderData_Fields.size() * 16 + 37 + 1 + this.ItemData_Fields.size() * 16;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRemoveInventoryObjects(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)28);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.FolderData_Fields.size());
        final Iterator<Object> iterator = this.FolderData_Fields.iterator();
        while (iterator.hasNext()) {
            this.packUUID(byteBuffer, iterator.next().FolderID);
        }
        byteBuffer.put((byte)this.ItemData_Fields.size());
        final Iterator<Object> iterator2 = this.ItemData_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().ItemID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final FolderData e = new FolderData();
            e.FolderID = this.unpackUUID(byteBuffer);
            this.FolderData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final ItemData e2 = new ItemData();
            e2.ItemID = this.unpackUUID(byteBuffer);
            this.ItemData_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class FolderData
    {
        public UUID FolderID;
    }
    
    public static class ItemData
    {
        public UUID ItemID;
    }
}
