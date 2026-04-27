// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarInterestsUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;
    
    public AvatarInterestsUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.PropertiesData_Field = new PropertiesData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarInterestsUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-81));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.PropertiesData_Field.WantToMask);
        this.packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1);
        this.packInt(byteBuffer, this.PropertiesData_Field.SkillsMask);
        this.packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1);
        this.packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.PropertiesData_Field.WantToMask = this.unpackInt(byteBuffer);
        this.PropertiesData_Field.WantToText = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.SkillsMask = this.unpackInt(byteBuffer);
        this.PropertiesData_Field.SkillsText = this.unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.LanguagesText = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class PropertiesData
    {
        public byte[] LanguagesText;
        public int SkillsMask;
        public byte[] SkillsText;
        public int WantToMask;
        public byte[] WantToText;
    }
}
