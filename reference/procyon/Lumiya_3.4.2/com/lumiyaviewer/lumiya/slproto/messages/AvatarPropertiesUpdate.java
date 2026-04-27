// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarPropertiesUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;
    
    public AvatarPropertiesUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.PropertiesData_Field = new PropertiesData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 34 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + 1 + 1 + this.PropertiesData_Field.ProfileURL.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarPropertiesUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-82));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.PropertiesData_Field.ImageID);
        this.packUUID(byteBuffer, this.PropertiesData_Field.FLImageID);
        this.packVariable(byteBuffer, this.PropertiesData_Field.AboutText, 2);
        this.packVariable(byteBuffer, this.PropertiesData_Field.FLAboutText, 1);
        this.packBoolean(byteBuffer, this.PropertiesData_Field.AllowPublish);
        this.packBoolean(byteBuffer, this.PropertiesData_Field.MaturePublish);
        this.packVariable(byteBuffer, this.PropertiesData_Field.ProfileURL, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.ImageID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.FLImageID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.AboutText = this.unpackVariable(byteBuffer, 2);
        this.PropertiesData_Field.FLAboutText = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.AllowPublish = this.unpackBoolean(byteBuffer);
        this.PropertiesData_Field.MaturePublish = this.unpackBoolean(byteBuffer);
        this.PropertiesData_Field.ProfileURL = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class PropertiesData
    {
        public byte[] AboutText;
        public boolean AllowPublish;
        public byte[] FLAboutText;
        public UUID FLImageID;
        public UUID ImageID;
        public boolean MaturePublish;
        public byte[] ProfileURL;
    }
}
