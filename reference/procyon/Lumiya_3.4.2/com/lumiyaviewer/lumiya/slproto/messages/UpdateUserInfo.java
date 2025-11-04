// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateUserInfo extends SLMessage
{
    public AgentData AgentData_Field;
    public UserData UserData_Field;
    
    public UpdateUserInfo() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.UserData_Field = new UserData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateUserInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-111));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        this.packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = this.unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class UserData
    {
        public byte[] DirectoryVisibility;
        public boolean IMViaEMail;
    }
}
