package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class InviteGroupResponse : SLMessage() {
    public InviteData InviteData_Field = InviteData()

    @JvmStatic
    class InviteData {
        public UUID AgentID
        public UUID GroupID
        public UUID InviteeID
        public Int MembershipFee
        public UUID RoleID
    }

    public InviteGroupResponse() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 72
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleInviteGroupResponse(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 94)
        packUUID(byteBuffer, this.InviteData_Field.AgentID)
        packUUID(byteBuffer, this.InviteData_Field.InviteeID)
        packUUID(byteBuffer, this.InviteData_Field.GroupID)
        packUUID(byteBuffer, this.InviteData_Field.RoleID)
        packInt(byteBuffer, this.InviteData_Field.MembershipFee)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.InviteData_Field.AgentID = unpackUUID(byteBuffer)
        this.InviteData_Field.InviteeID = unpackUUID(byteBuffer)
        this.InviteData_Field.GroupID = unpackUUID(byteBuffer)
        this.InviteData_Field.RoleID = unpackUUID(byteBuffer)
        this.InviteData_Field.MembershipFee = unpackInt(byteBuffer)
    }
}
