package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class KillChildAgents extends SLMessage {
    public IDBlock IDBlock_Field = new IDBlock();

    public static class IDBlock {
        public UUID AgentID;
    }

    public KillChildAgents() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKillChildAgents(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -14);
        packUUID(byteBuffer, this.IDBlock_Field.AgentID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.IDBlock_Field.AgentID = unpackUUID(byteBuffer);
    }
}
