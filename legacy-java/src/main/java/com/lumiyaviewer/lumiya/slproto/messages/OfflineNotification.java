package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class OfflineNotification extends SLMessage {
    public ArrayList<AgentBlock> AgentBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentBlock {
        public UUID AgentID;
    }

    public OfflineNotification() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.AgentBlock_Fields.size() * 16) + 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfflineNotification(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 67);
        byteBuffer.put((byte) this.AgentBlock_Fields.size());
        Iterator<T> it = this.AgentBlock_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((AgentBlock) it.next()).AgentID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AgentBlock agentBlock = new AgentBlock();
            agentBlock.AgentID = unpackUUID(byteBuffer);
            this.AgentBlock_Fields.add(agentBlock);
        }
    }
}
