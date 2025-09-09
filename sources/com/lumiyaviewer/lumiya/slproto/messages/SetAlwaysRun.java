package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetAlwaysRun extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID AgentID;
        public boolean AlwaysRun;
        public UUID SessionID;
    }

    public SetAlwaysRun() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetAlwaysRun(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 88);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer);
    }
}
