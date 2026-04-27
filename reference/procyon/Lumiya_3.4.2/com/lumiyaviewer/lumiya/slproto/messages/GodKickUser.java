// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GodKickUser extends SLMessage
{
    public UserInfo UserInfo_Field;
    
    public GodKickUser() {
        this.zeroCoded = false;
        this.UserInfo_Field = new UserInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 54 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGodKickUser(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-91));
        this.packUUID(byteBuffer, this.UserInfo_Field.GodID);
        this.packUUID(byteBuffer, this.UserInfo_Field.GodSessionID);
        this.packUUID(byteBuffer, this.UserInfo_Field.AgentID);
        this.packInt(byteBuffer, this.UserInfo_Field.KickFlags);
        this.packVariable(byteBuffer, this.UserInfo_Field.Reason, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.UserInfo_Field.GodID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.GodSessionID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.AgentID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.KickFlags = this.unpackInt(byteBuffer);
        this.UserInfo_Field.Reason = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class UserInfo
    {
        public UUID AgentID;
        public UUID GodID;
        public UUID GodSessionID;
        public int KickFlags;
        public byte[] Reason;
    }
}
