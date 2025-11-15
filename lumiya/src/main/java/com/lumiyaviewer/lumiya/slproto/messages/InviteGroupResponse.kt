package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InviteGroupResponse : SLMessage {
    InviteData InviteData_Field = InviteData()

    class InviteData {
        UUID AgentID
        UUID GroupID
        UUID InviteeID
        Int MembershipFee
        UUID RoleID
    }

    InviteGroupResponse() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 72
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupResponse(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 94)
        packUUID(byteBuffer, this.InviteData_Field.AgentID)
        packUUID(byteBuffer, this.InviteData_Field.InviteeID)
        packUUID(byteBuffer, this.InviteData_Field.GroupID)
        packUUID(byteBuffer, this.InviteData_Field.RoleID)
        packInt(byteBuffer, this.InviteData_Field.MembershipFee)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.InviteData_Field.AgentID = unpackUUID(byteBuffer)
        this.InviteData_Field.InviteeID = unpackUUID(byteBuffer)
        this.InviteData_Field.GroupID = unpackUUID(byteBuffer)
        this.InviteData_Field.RoleID = unpackUUID(byteBuffer)
        this.InviteData_Field.MembershipFee = unpackInt(byteBuffer)
    }
}
