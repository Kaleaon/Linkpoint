package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class OfflineNotification extends SLMessage {
    public ArrayList<AgentBlock> AgentBlock_Fields = new ArrayList<>();

    public static class AgentBlock {
        public UUID AgentID;
    }

    public OfflineNotification() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.AgentBlock_Fields.size() * 16) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfflineNotification(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 67);
        byteBuffer.put((byte) this.AgentBlock_Fields.size());
        for (AgentBlock agentBlock : this.AgentBlock_Fields) {
            packUUID(byteBuffer, agentBlock.AgentID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            AgentBlock agentBlock = new AgentBlock();
            agentBlock.AgentID = unpackUUID(byteBuffer);
            this.AgentBlock_Fields.add(agentBlock);
        }
    }
}
