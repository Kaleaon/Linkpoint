package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupMembersReply extends SLMessage {
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<MemberData> MemberData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class GroupData {
        public UUID GroupID;
        public int MemberCount;
        public UUID RequestID;
    }

    public static class MemberData {
        public UUID AgentID;
        public long AgentPowers;
        public int Contribution;
        public boolean IsOwner;
        public byte[] OnlineStatus;
        public byte[] Title;
    }

    public GroupMembersReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize() {
        int i = 57;
        Iterator<T> it = this.MemberData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            MemberData memberData = (MemberData) it.next();
            i = memberData.Title.length + memberData.OnlineStatus.length + 21 + 8 + 1 + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupMembersReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 111);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        packUUID(byteBuffer, this.GroupData_Field.RequestID);
        packInt(byteBuffer, this.GroupData_Field.MemberCount);
        byteBuffer.put((byte) this.MemberData_Fields.size());
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.AgentID);
            packInt(byteBuffer, memberData.Contribution);
            packVariable(byteBuffer, memberData.OnlineStatus, 1);
            packLong(byteBuffer, memberData.AgentPowers);
            packVariable(byteBuffer, memberData.Title, 1);
            packBoolean(byteBuffer, memberData.IsOwner);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer);
        this.GroupData_Field.MemberCount = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            MemberData memberData = new MemberData();
            memberData.AgentID = unpackUUID(byteBuffer);
            memberData.Contribution = unpackInt(byteBuffer);
            memberData.OnlineStatus = unpackVariable(byteBuffer, 1);
            memberData.AgentPowers = unpackLong(byteBuffer);
            memberData.Title = unpackVariable(byteBuffer, 1);
            memberData.IsOwner = unpackBoolean(byteBuffer);
            this.MemberData_Fields.add(memberData);
        }
    }
}
