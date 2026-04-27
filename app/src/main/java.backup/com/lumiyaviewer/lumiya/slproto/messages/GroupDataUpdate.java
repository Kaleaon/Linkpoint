package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupDataUpdate extends SLMessage {
    public ArrayList<AgentGroupData> AgentGroupData_Fields = new ArrayList<>();

    public static class AgentGroupData {
        public UUID AgentID;
        public long AgentPowers;
        public UUID GroupID;
        public byte[] GroupTitle;
    }

    public GroupDataUpdate() {
        this.zeroCoded = true;
    }

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

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupDataUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
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

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            AgentGroupData agentGroupData = new AgentGroupData();
            agentGroupData.AgentID = unpackUUID(byteBuffer);
            agentGroupData.GroupID = unpackUUID(byteBuffer);
            agentGroupData.AgentPowers = unpackLong(byteBuffer);
            agentGroupData.GroupTitle = unpackVariable(byteBuffer, 1);
            this.AgentGroupData_Fields.add(agentGroupData);
        }
    }
}
