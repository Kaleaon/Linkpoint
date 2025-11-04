// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UserInfoReply extends SLMessage
{
    public AgentData AgentData_Field;
    public UserData UserData_Field;
    
    public UserInfoReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.UserData_Field = new UserData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 2 + this.UserData_Field.EMail.length + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUserInfoReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-112));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        this.packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
        this.packVariable(byteBuffer, this.UserData_Field.EMail, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = this.unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = this.unpackVariable(byteBuffer, 1);
        this.UserData_Field.EMail = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class UserData
    {
        public byte[] DirectoryVisibility;
        public byte[] EMail;
        public boolean IMViaEMail;
    }
}
