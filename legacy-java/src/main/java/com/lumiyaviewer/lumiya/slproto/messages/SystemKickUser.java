package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class SystemKickUser extends SLMessage {
    public ArrayList<AgentInfo> AgentInfo_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentInfo {
        public UUID AgentID;
    }

    public SystemKickUser() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.AgentInfo_Fields.size() * 16) + 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSystemKickUser(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -90);
        byteBuffer.put((byte) this.AgentInfo_Fields.size());
        Iterator<T> it = this.AgentInfo_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((AgentInfo) it.next()).AgentID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AgentInfo agentInfo = new AgentInfo();
            agentInfo.AgentID = unpackUUID(byteBuffer);
            this.AgentInfo_Fields.add(agentInfo);
        }
    }
}
