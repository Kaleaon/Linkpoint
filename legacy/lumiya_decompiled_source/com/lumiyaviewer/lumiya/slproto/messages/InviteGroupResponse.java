package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class InviteGroupResponse extends SLMessage {
    public InviteData InviteData_Field;

    /* loaded from: classes.dex */
    public static class InviteData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID InviteeID;
        public int MembershipFee;
        public UUID RoleID;
    }

    public InviteGroupResponse() {
        this.zeroCoded = false;
        this.InviteData_Field = new InviteData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 72;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupResponse(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 94);
        packUUID(byteBuffer, this.InviteData_Field.AgentID);
        packUUID(byteBuffer, this.InviteData_Field.InviteeID);
        packUUID(byteBuffer, this.InviteData_Field.GroupID);
        packUUID(byteBuffer, this.InviteData_Field.RoleID);
        packInt(byteBuffer, this.InviteData_Field.MembershipFee);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InviteData_Field.AgentID = unpackUUID(byteBuffer);
        this.InviteData_Field.InviteeID = unpackUUID(byteBuffer);
        this.InviteData_Field.GroupID = unpackUUID(byteBuffer);
        this.InviteData_Field.RoleID = unpackUUID(byteBuffer);
        this.InviteData_Field.MembershipFee = unpackInt(byteBuffer);
    }
}
