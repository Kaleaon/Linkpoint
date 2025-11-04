// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateInventoryFolder extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields;
    
    public UpdateInventoryFolder() {
        this.FolderData_Fields = new ArrayList<FolderData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.FolderData_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            n += iterator.next().Name.length + 34;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateInventoryFolder(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)18);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.FolderData_Fields.size());
        for (final FolderData folderData : this.FolderData_Fields) {
            this.packUUID(byteBuffer, folderData.FolderID);
            this.packUUID(byteBuffer, folderData.ParentID);
            this.packByte(byteBuffer, (byte)folderData.Type);
            this.packVariable(byteBuffer, folderData.Name, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final FolderData e = new FolderData();
            e.FolderID = this.unpackUUID(byteBuffer);
            e.ParentID = this.unpackUUID(byteBuffer);
            e.Type = this.unpackByte(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            this.FolderData_Fields.add(e);
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
        public byte[] Name;
        public UUID ParentID;
        public int Type;
    }
}
