package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MuteListRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MuteData MuteData_Field = new MuteData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class MuteData {
        public int MuteCRC;
    }

    public MuteListRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMuteListRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 6);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.MuteData_Field.MuteCRC);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MuteData_Field.MuteCRC = unpackInt(byteBuffer);
    }
}
