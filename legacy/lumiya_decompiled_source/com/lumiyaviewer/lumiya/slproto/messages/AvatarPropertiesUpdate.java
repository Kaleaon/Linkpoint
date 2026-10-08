package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AvatarPropertiesUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public PropertiesData PropertiesData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class PropertiesData {
        public byte[] AboutText;
        public boolean AllowPublish;
        public byte[] FLAboutText;
        public UUID FLImageID;
        public UUID ImageID;
        public boolean MaturePublish;
        public byte[] ProfileURL;
    }

    public AvatarPropertiesUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.PropertiesData_Field = new PropertiesData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.PropertiesData_Field.AboutText.length + 34 + 1 + this.PropertiesData_Field.FLAboutText.length + 1 + 1 + 1 + this.PropertiesData_Field.ProfileURL.length + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -82);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.PropertiesData_Field.ImageID);
        packUUID(byteBuffer, this.PropertiesData_Field.FLImageID);
        packVariable(byteBuffer, this.PropertiesData_Field.AboutText, 2);
        packVariable(byteBuffer, this.PropertiesData_Field.FLAboutText, 1);
        packBoolean(byteBuffer, this.PropertiesData_Field.AllowPublish);
        packBoolean(byteBuffer, this.PropertiesData_Field.MaturePublish);
        packVariable(byteBuffer, this.PropertiesData_Field.ProfileURL, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.PropertiesData_Field.ImageID = unpackUUID(byteBuffer);
        this.PropertiesData_Field.FLImageID = unpackUUID(byteBuffer);
        this.PropertiesData_Field.AboutText = unpackVariable(byteBuffer, 2);
        this.PropertiesData_Field.FLAboutText = unpackVariable(byteBuffer, 1);
        this.PropertiesData_Field.AllowPublish = unpackBoolean(byteBuffer);
        this.PropertiesData_Field.MaturePublish = unpackBoolean(byteBuffer);
        this.PropertiesData_Field.ProfileURL = unpackVariable(byteBuffer, 1);
    }
}
