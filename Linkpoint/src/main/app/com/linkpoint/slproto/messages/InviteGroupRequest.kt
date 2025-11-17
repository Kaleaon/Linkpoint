package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class InviteGroupRequest : SLMessage {
    AgentData AgentData_Field
    GroupData GroupData_Field
    ArrayList<InviteData> InviteData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GroupData {
        UUID GroupID
    }

    class InviteData {
        UUID InviteeID
        UUID RoleID
    }

    InviteGroupRequest() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    Int CalcPayloadSize() {
        return (this.InviteData_Fields.size() * 32) + 53
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 93)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        byteBuffer.put((byte) this.InviteData_Fields.size())
        for (InviteData inviteData : this.InviteData_Fields) {
            packUUID(byteBuffer, inviteData.InviteeID)
            packUUID(byteBuffer, inviteData.RoleID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InviteData inviteData = InviteData()
            inviteData.InviteeID = unpackUUID(byteBuffer)
            inviteData.RoleID = unpackUUID(byteBuffer)
            this.InviteData_Fields.add(inviteData)
        }
    }
}
