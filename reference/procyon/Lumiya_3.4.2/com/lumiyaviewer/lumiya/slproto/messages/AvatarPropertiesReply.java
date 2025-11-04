// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarPropertiesReply extends SLMessage
{
    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;
    
    public AvatarPropertiesReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.PropertiesData_Field = new PropertiesData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 50 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + this.PropertiesData_Field.BornOn.length + 1 + this.PropertiesData_Field.ProfileURL.length + 1 + this.PropertiesData_Field.CharterMember.length + 4 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarPropertiesReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-85));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        this.packUUID(byteBuffer, this.PropertiesData_Field.ImageID);
        this.packUUID(byteBuffer, this.PropertiesData_Field.FLImageID);
        this.packUUID(byteBuffer, this.PropertiesData_Field.PartnerID);
        this.packVariable(byteBuffer, this.PropertiesData_Field.AboutText, 2);
        this.packVariable(byteBuffer, this.PropertiesData_Field.FLAboutText, 1);
        this.packVariable(byteBuffer, this.PropertiesData_Field.BornOn, 1);
        this.packVariable(byteBuffer, this.PropertiesData_Field.ProfileURL, 1);
        this.packVariable(byteBuffer, this.PropertiesData_Field.CharterMember, 1);
        this.packInt(byteBuffer, this.PropertiesData_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.ImageID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.FLImageID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.PartnerID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.AboutText = this.unpackVariable(byteBuffer, 2);
        this.PropertiesData_Field.FLAboutText = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.BornOn = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.ProfileURL = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.CharterMember = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.Flags = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID AvatarID;
    }
    
    public static class PropertiesData
    {
        public byte[] AboutText;
        public byte[] BornOn;
        public byte[] CharterMember;
        public byte[] FLAboutText;
        public UUID FLImageID;
        public int Flags;
        public UUID ImageID;
        public UUID PartnerID;
        public byte[] ProfileURL;
    }
}
