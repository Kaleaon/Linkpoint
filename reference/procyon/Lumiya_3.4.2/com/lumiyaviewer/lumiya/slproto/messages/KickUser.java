// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class KickUser extends SLMessage
{
    public TargetBlock TargetBlock_Field;
    public UserInfo UserInfo_Field;
    
    public KickUser() {
        this.zeroCoded = false;
        this.TargetBlock_Field = new TargetBlock();
        this.UserInfo_Field = new UserInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 34 + 10;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleKickUser(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-93));
        this.packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP);
        this.packShort(byteBuffer, (short)this.TargetBlock_Field.TargetPort);
        this.packUUID(byteBuffer, this.UserInfo_Field.AgentID);
        this.packUUID(byteBuffer, this.UserInfo_Field.SessionID);
        this.packVariable(byteBuffer, this.UserInfo_Field.Reason, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TargetBlock_Field.TargetIP = this.unpackIPAddress(byteBuffer);
        this.TargetBlock_Field.TargetPort = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.UserInfo_Field.AgentID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.SessionID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.Reason = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class TargetBlock
    {
        public Inet4Address TargetIP;
        public int TargetPort;
    }
    
    public static class UserInfo
    {
        public UUID AgentID;
        public byte[] Reason;
        public UUID SessionID;
    }
}
