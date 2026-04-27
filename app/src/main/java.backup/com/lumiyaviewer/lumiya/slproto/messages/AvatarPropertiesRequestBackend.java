package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AvatarPropertiesRequestBackend extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID AgentID;
        public UUID AvatarID;
        public int GodLevel;
        public boolean WebProfilesDisabled;
    }

    public AvatarPropertiesRequestBackend() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 38;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPropertiesRequestBackend(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -86);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        packByte(byteBuffer, (byte) this.AgentData_Field.GodLevel);
        packBoolean(byteBuffer, this.AgentData_Field.WebProfilesDisabled);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer);
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentData_Field.WebProfilesDisabled = unpackBoolean(byteBuffer);
    }
}
