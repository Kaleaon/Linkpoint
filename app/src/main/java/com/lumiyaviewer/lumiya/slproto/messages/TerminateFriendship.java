package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TerminateFriendship extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ExBlock ExBlock_Field = new ExBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ExBlock {
        public UUID OtherID;
    }

    public TerminateFriendship() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTerminateFriendship(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 44);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.ExBlock_Field.OtherID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ExBlock_Field.OtherID = unpackUUID(byteBuffer);
    }
}
