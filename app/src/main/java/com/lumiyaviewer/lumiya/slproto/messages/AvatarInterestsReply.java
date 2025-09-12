package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AvatarInterestsReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public PropertiesData PropertiesData_Field = new PropertiesData();

    public static class AgentData {
        public UUID AgentID;
        public UUID AvatarID;
    }

    public static class PropertiesData {
        public byte[] LanguagesText;
        public int SkillsMask;
        public byte[] SkillsText;
        public int WantToMask;
        public byte[] WantToText;
    }

    public AvatarInterestsReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -84);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask);
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1);
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask);
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1);
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer);
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer);
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer);
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1);
    }
}
