package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GrantGodlikePowers extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public GrantData GrantData_Field = new GrantData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class GrantData {
        public int GodLevel;
        public UUID Token;
    }

    public GrantGodlikePowers() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGrantGodlikePowers(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.GrantData_Field.GodLevel);
        packUUID(byteBuffer, this.GrantData_Field.Token);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.GrantData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.GrantData_Field.Token = unpackUUID(byteBuffer);
    }
}
