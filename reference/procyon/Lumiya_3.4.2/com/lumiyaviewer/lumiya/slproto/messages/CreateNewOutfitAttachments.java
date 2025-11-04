// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateNewOutfitAttachments extends SLMessage
{
    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList<ObjectData> ObjectData_Fields;
    
    public CreateNewOutfitAttachments() {
        this.ObjectData_Fields = new ArrayList<ObjectData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.HeaderData_Field = new HeaderData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Fields.size() * 32 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateNewOutfitAttachments(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-114));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.HeaderData_Field.NewFolderID);
        byteBuffer.put((byte)this.ObjectData_Fields.size());
        for (final ObjectData objectData : this.ObjectData_Fields) {
            this.packUUID(byteBuffer, objectData.OldItemID);
            this.packUUID(byteBuffer, objectData.OldFolderID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.HeaderData_Field.NewFolderID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final ObjectData e = new ObjectData();
            e.OldItemID = this.unpackUUID(byteBuffer);
            e.OldFolderID = this.unpackUUID(byteBuffer);
            this.ObjectData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class HeaderData
    {
        public UUID NewFolderID;
    }
    
    public static class ObjectData
    {
        public UUID OldFolderID;
        public UUID OldItemID;
    }
}
