package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class GroupRoleMembersReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<MemberData> MemberData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;
        public int TotalPairs;
    }

    public static class MemberData {
        public UUID MemberID;
        public UUID RoleID;
    }

    public GroupRoleMembersReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.MemberData_Fields.size() * 32) + 57;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleMembersReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 118);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.AgentData_Field.RequestID);
        packInt(byteBuffer, this.AgentData_Field.TotalPairs);
        byteBuffer.put((byte) this.MemberData_Fields.size());
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.RoleID);
            packUUID(byteBuffer, memberData.MemberID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer);
        this.AgentData_Field.TotalPairs = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            MemberData memberData = new MemberData();
            memberData.RoleID = unpackUUID(byteBuffer);
            memberData.MemberID = unpackUUID(byteBuffer);
            this.MemberData_Fields.add(memberData);
        }
    }
}
