package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class InviteGroupResponse extends SLMessage {
    public InviteData InviteData_Field = new InviteData();

    public static class InviteData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID InviteeID;
        public int MembershipFee;
        public UUID RoleID;
    }

    public InviteGroupResponse() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupResponse(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 94);
        packUUID(byteBuffer, this.InviteData_Field.AgentID);
        packUUID(byteBuffer, this.InviteData_Field.InviteeID);
        packUUID(byteBuffer, this.InviteData_Field.GroupID);
        packUUID(byteBuffer, this.InviteData_Field.RoleID);
        packInt(byteBuffer, this.InviteData_Field.MembershipFee);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InviteData_Field.AgentID = unpackUUID(byteBuffer);
        this.InviteData_Field.InviteeID = unpackUUID(byteBuffer);
        this.InviteData_Field.GroupID = unpackUUID(byteBuffer);
        this.InviteData_Field.RoleID = unpackUUID(byteBuffer);
        this.InviteData_Field.MembershipFee = unpackInt(byteBuffer);
    }
}
