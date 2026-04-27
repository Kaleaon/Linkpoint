// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateInventoryFolder extends SLMessage
{
    public AgentData AgentData_Field;
    public FolderData FolderData_Field;
    
    public CreateInventoryFolder() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FolderData_Field = new FolderData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.FolderData_Field.Name.length + 34 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateInventoryFolder(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)17);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.FolderData_Field.FolderID);
        this.packUUID(byteBuffer, this.FolderData_Field.ParentID);
        this.packByte(byteBuffer, (byte)this.FolderData_Field.Type);
        this.packVariable(byteBuffer, this.FolderData_Field.Name, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.FolderData_Field.FolderID = this.unpackUUID(byteBuffer);
        this.FolderData_Field.ParentID = this.unpackUUID(byteBuffer);
        this.FolderData_Field.Type = this.unpackByte(byteBuffer);
        this.FolderData_Field.Name = this.unpackVariable(byteBuffer, 1);
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
