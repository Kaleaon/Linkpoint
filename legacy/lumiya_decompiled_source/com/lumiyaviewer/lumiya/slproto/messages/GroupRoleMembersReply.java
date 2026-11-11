package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class GroupRoleMembersReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<MemberData> MemberData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;
        public int TotalPairs;
    }

    /* loaded from: classes.dex */
    public static class MemberData {
        public UUID MemberID;
        public UUID RoleID;
    }

    public GroupRoleMembersReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.MemberData_Fields.size() * 32) + 57;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleMembersReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer);
        this.AgentData_Field.TotalPairs = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            MemberData memberData = new MemberData();
            memberData.RoleID = unpackUUID(byteBuffer);
            memberData.MemberID = unpackUUID(byteBuffer);
            this.MemberData_Fields.add(memberData);
        }
    }
}
