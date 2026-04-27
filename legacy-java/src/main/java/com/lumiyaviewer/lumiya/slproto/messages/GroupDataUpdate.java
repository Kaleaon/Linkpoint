package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class GroupDataUpdate extends SLMessage {
    public ArrayList<AgentGroupData> AgentGroupData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentGroupData {
        public UUID AgentID;
        public long AgentPowers;
        public UUID GroupID;
        public byte[] GroupTitle;
    }

    public GroupDataUpdate() {
        this.zeroCoded = true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.AgentGroupData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((AgentGroupData) it.next()).GroupTitle.length + 41 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupDataUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -124);
        byteBuffer.put((byte) this.AgentGroupData_Fields.size());
        for (AgentGroupData agentGroupData : this.AgentGroupData_Fields) {
            packUUID(byteBuffer, agentGroupData.AgentID);
            packUUID(byteBuffer, agentGroupData.GroupID);
            packLong(byteBuffer, agentGroupData.AgentPowers);
            packVariable(byteBuffer, agentGroupData.GroupTitle, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AgentGroupData agentGroupData = new AgentGroupData();
            agentGroupData.AgentID = unpackUUID(byteBuffer);
            agentGroupData.GroupID = unpackUUID(byteBuffer);
            agentGroupData.AgentPowers = unpackLong(byteBuffer);
            agentGroupData.GroupTitle = unpackVariable(byteBuffer, 1);
            this.AgentGroupData_Fields.add(agentGroupData);
        }
    }
}
