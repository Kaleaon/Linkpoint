package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class AvatarInterestsUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class PropertiesData {
        public byte[] LanguagesText;
        public int SkillsMask;
        public byte[] SkillsText;
        public int WantToMask;
        public byte[] WantToText;
    }

    public AvatarInterestsUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.PropertiesData_Field = new PropertiesData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.PropertiesData_Field.WantToText.length + 5 + 4 + 1 + this.PropertiesData_Field.SkillsText.length + 1 + this.PropertiesData_Field.LanguagesText.length + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarInterestsUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -81);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.PropertiesData_Field.WantToMask);
        packVariable(byteBuffer, this.PropertiesData_Field.WantToText, 1);
        packInt(byteBuffer, this.PropertiesData_Field.SkillsMask);
        packVariable(byteBuffer, this.PropertiesData_Field.SkillsText, 1);
        packVariable(byteBuffer, this.PropertiesData_Field.LanguagesText, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.PropertiesData_Field.WantToMask = unpackInt(byteBuffer);
        this.PropertiesData_Field.WantToText = unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.SkillsMask = unpackInt(byteBuffer);
        this.PropertiesData_Field.SkillsText = unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.LanguagesText = unpackVariable(byteBuffer, 1);
    }
}
