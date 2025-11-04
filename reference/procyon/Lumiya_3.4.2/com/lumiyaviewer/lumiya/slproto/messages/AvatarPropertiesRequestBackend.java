// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarPropertiesRequestBackend extends SLMessage
{
    public AgentData AgentData_Field;
    
    public AvatarPropertiesRequestBackend() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 38;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarPropertiesRequestBackend(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-86));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.GodLevel);
        this.packBoolean(byteBuffer, this.AgentData_Field.WebProfilesDisabled);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GodLevel = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentData_Field.WebProfilesDisabled = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID AvatarID;
        public int GodLevel;
        public boolean WebProfilesDisabled;
    }
}
