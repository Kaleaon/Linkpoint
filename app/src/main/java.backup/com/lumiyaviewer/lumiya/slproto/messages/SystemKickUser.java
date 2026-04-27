package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class SystemKickUser extends SLMessage {
    public ArrayList<AgentInfo> AgentInfo_Fields = new ArrayList<>();

    public static class AgentInfo {
        public UUID AgentID;
    }

    public SystemKickUser() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.AgentInfo_Fields.size() * 16) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSystemKickUser(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -90);
        byteBuffer.put((byte) this.AgentInfo_Fields.size());
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packUUID(byteBuffer, agentInfo.AgentID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            AgentInfo agentInfo = new AgentInfo();
            agentInfo.AgentID = unpackUUID(byteBuffer);
            this.AgentInfo_Fields.add(agentInfo);
        }
    }
}
