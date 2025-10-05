package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class UpdateGroupInfo : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public GroupData GroupData_Field = GroupData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class GroupData {
        public Boolean AllowPublish
        public Byte[] Charter
        public UUID GroupID
        public UUID InsigniaID
        public Boolean MaturePublish
        public Int MembershipFee
        public Boolean OpenEnrollment
        public Boolean ShowInList
    }

    public UpdateGroupInfo() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.GroupData_Field.Charter.length + 18 + 1 + 16 + 4 + 1 + 1 + 1 + 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateGroupInfo(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 85)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packVariable(byteBuffer, this.GroupData_Field.Charter, 2)
        packBoolean(byteBuffer, this.GroupData_Field.ShowInList)
        packUUID(byteBuffer, this.GroupData_Field.InsigniaID)
        packInt(byteBuffer, this.GroupData_Field.MembershipFee)
        packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment)
        packBoolean(byteBuffer, this.GroupData_Field.AllowPublish)
        packBoolean(byteBuffer, this.GroupData_Field.MaturePublish)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.Charter = unpackVariable(byteBuffer, 2)
        this.GroupData_Field.ShowInList = unpackBoolean(byteBuffer)
        this.GroupData_Field.InsigniaID = unpackUUID(byteBuffer)
        this.GroupData_Field.MembershipFee = unpackInt(byteBuffer)
        this.GroupData_Field.OpenEnrollment = unpackBoolean(byteBuffer)
        this.GroupData_Field.AllowPublish = unpackBoolean(byteBuffer)
        this.GroupData_Field.MaturePublish = unpackBoolean(byteBuffer)
    }
}
