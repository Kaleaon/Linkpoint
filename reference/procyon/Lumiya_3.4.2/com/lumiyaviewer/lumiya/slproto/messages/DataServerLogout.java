// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DataServerLogout extends SLMessage
{
    public UserData UserData_Field;
    
    public DataServerLogout() {
        this.zeroCoded = false;
        this.UserData_Field = new UserData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 41;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDataServerLogout(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-5));
        this.packUUID(byteBuffer, this.UserData_Field.AgentID);
        this.packIPAddress(byteBuffer, this.UserData_Field.ViewerIP);
        this.packBoolean(byteBuffer, this.UserData_Field.Disconnect);
        this.packUUID(byteBuffer, this.UserData_Field.SessionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.UserData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.UserData_Field.ViewerIP = this.unpackIPAddress(byteBuffer);
        this.UserData_Field.Disconnect = this.unpackBoolean(byteBuffer);
        this.UserData_Field.SessionID = this.unpackUUID(byteBuffer);
    }
    
    public static class UserData
    {
        public UUID AgentID;
        public boolean Disconnect;
        public UUID SessionID;
        public Inet4Address ViewerIP;
    }
}
