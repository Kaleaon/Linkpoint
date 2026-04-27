// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetGroupAcceptNotices extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    public NewData NewData_Field;
    
    public SetGroupAcceptNotices() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
        this.NewData_Field = new NewData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 54;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetGroupAcceptNotices(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)114);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.Data_Field.GroupID);
        this.packBoolean(byteBuffer, this.Data_Field.AcceptNotices);
        this.packBoolean(byteBuffer, this.NewData_Field.ListInProfile);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Data_Field.GroupID = this.unpackUUID(byteBuffer);
        this.Data_Field.AcceptNotices = this.unpackBoolean(byteBuffer);
        this.NewData_Field.ListInProfile = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public boolean AcceptNotices;
        public UUID GroupID;
    }
    
    public static class NewData
    {
        public boolean ListInProfile;
    }
}
