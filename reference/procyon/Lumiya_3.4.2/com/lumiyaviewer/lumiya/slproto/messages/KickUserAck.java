// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class KickUserAck extends SLMessage
{
    public UserInfo UserInfo_Field;
    
    public KickUserAck() {
        this.zeroCoded = false;
        this.UserInfo_Field = new UserInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleKickUserAck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-92));
        this.packUUID(byteBuffer, this.UserInfo_Field.SessionID);
        this.packInt(byteBuffer, this.UserInfo_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.UserInfo_Field.SessionID = this.unpackUUID(byteBuffer);
        this.UserInfo_Field.Flags = this.unpackInt(byteBuffer);
    }
    
    public static class UserInfo
    {
        public int Flags;
        public UUID SessionID;
    }
}
