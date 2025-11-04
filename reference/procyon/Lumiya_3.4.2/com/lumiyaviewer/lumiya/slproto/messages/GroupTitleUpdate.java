// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupTitleUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    
    public GroupTitleUpdate() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 68;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupTitleUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)121);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.AgentData_Field.TitleRoleID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.TitleRoleID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
        public UUID TitleRoleID;
    }
}
