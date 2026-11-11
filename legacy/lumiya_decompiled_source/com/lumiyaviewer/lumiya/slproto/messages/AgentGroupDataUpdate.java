package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
/* loaded from: classes.dex */
public class AgentGroupDataUpdate extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class GroupData {
        public boolean AcceptNotices;
        public int Contribution;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte[] GroupName;
        public long GroupPowers;
    }

    public AgentGroupDataUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 21;
        Iterator<T> it = this.GroupData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((GroupData) it.next()).GroupName.length + 46 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentGroupDataUpdate(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -123);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        byteBuffer.put((byte) this.GroupData_Fields.size());
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID);
            packLong(byteBuffer, groupData.GroupPowers);
            packBoolean(byteBuffer, groupData.AcceptNotices);
            packUUID(byteBuffer, groupData.GroupInsigniaID);
            packInt(byteBuffer, groupData.Contribution);
            packVariable(byteBuffer, groupData.GroupName, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            GroupData groupData = new GroupData();
            groupData.GroupID = unpackUUID(byteBuffer);
            groupData.GroupPowers = unpackLong(byteBuffer);
            groupData.AcceptNotices = unpackBoolean(byteBuffer);
            groupData.GroupInsigniaID = unpackUUID(byteBuffer);
            groupData.Contribution = unpackInt(byteBuffer);
            groupData.GroupName = unpackVariable(byteBuffer, 1);
            this.GroupData_Fields.add(groupData);
        }
    }
}
