package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class InviteGroupRequest extends SLMessage {
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<InviteData> InviteData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class GroupData {
        public UUID GroupID;
    }

    public static class InviteData {
        public UUID InviteeID;
        public UUID RoleID;
    }

    public InviteGroupRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize() {
        return (this.InviteData_Fields.size() * 32) + 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 93);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        byteBuffer.put((byte) this.InviteData_Fields.size());
        for (InviteData inviteData : this.InviteData_Fields) {
            packUUID(byteBuffer, inviteData.InviteeID);
            packUUID(byteBuffer, inviteData.RoleID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            InviteData inviteData = new InviteData();
            inviteData.InviteeID = unpackUUID(byteBuffer);
            inviteData.RoleID = unpackUUID(byteBuffer);
            this.InviteData_Fields.add(inviteData);
        }
    }
}
