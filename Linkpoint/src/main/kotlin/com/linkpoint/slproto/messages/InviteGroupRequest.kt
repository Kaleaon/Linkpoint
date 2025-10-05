package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class InviteGroupRequest : SLMessage() {
    public AgentData AgentData_Field
    public GroupData GroupData_Field
    public ArrayList<InviteData> InviteData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class GroupData {
        public UUID GroupID
    }

    @JvmStatic
    class InviteData {
        public UUID InviteeID
        public UUID RoleID
    }

    public InviteGroupRequest() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    public Int CalcPayloadSize() {
        return (this.InviteData_Fields.size() * 32) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInviteGroupRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 93)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        byteBuffer.put((Byte) this.InviteData_Fields.size())
        for (InviteData inviteData : this.InviteData_Fields) {
            packUUID(byteBuffer, inviteData.InviteeID)
            packUUID(byteBuffer, inviteData.RoleID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InviteData inviteData = InviteData()
            inviteData.InviteeID = unpackUUID(byteBuffer)
            inviteData.RoleID = unpackUUID(byteBuffer)
            this.InviteData_Fields.add(inviteData)
        }
    }
}
