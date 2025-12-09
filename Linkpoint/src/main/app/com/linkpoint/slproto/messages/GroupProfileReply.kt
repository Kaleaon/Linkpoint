package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupProfileReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        Boolean AllowPublish
        ByteArray Charter
        UUID FounderID
        UUID GroupID
        Int GroupMembershipCount
        Int GroupRolesCount
        UUID InsigniaID
        Boolean MaturePublish
        ByteArray MemberTitle
        Int MembershipFee
        Int Money
        ByteArray Name
        Boolean OpenEnrollment
        UUID OwnerRole
        Long PowersMask
        Boolean ShowInList
    }

    GroupProfileReply() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.GroupData_Field.Name.size + 17 + 2 + this.GroupData_Field.Charter.size + 1 + 1 + this.GroupData_Field.MemberTitle.size + 8 + 16 + 16 + 4 + 1 + 4 + 4 + 4 + 1 + 1 + 16 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupProfileReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 96)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packVariable(byteBuffer, this.GroupData_Field.Name, 1)
        packVariable(byteBuffer, this.GroupData_Field.Charter, 2)
        packBoolean(byteBuffer, this.GroupData_Field.ShowInList)
        packVariable(byteBuffer, this.GroupData_Field.MemberTitle, 1)
        packLong(byteBuffer, this.GroupData_Field.PowersMask)
        packUUID(byteBuffer, this.GroupData_Field.InsigniaID)
        packUUID(byteBuffer, this.GroupData_Field.FounderID)
        packInt(byteBuffer, this.GroupData_Field.MembershipFee)
        packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment)
        packInt(byteBuffer, this.GroupData_Field.Money)
        packInt(byteBuffer, this.GroupData_Field.GroupMembershipCount)
        packInt(byteBuffer, this.GroupData_Field.GroupRolesCount)
        packBoolean(byteBuffer, this.GroupData_Field.AllowPublish)
        packBoolean(byteBuffer, this.GroupData_Field.MaturePublish)
        packUUID(byteBuffer, this.GroupData_Field.OwnerRole)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.Name = unpackVariable(byteBuffer, 1)
        this.GroupData_Field.Charter = unpackVariable(byteBuffer, 2)
        this.GroupData_Field.ShowInList = unpackBoolean(byteBuffer)
        this.GroupData_Field.MemberTitle = unpackVariable(byteBuffer, 1)
        this.GroupData_Field.PowersMask = unpackLong(byteBuffer)
        this.GroupData_Field.InsigniaID = unpackUUID(byteBuffer)
        this.GroupData_Field.FounderID = unpackUUID(byteBuffer)
        this.GroupData_Field.MembershipFee = unpackInt(byteBuffer)
        this.GroupData_Field.OpenEnrollment = unpackBoolean(byteBuffer)
        this.GroupData_Field.Money = unpackInt(byteBuffer)
        this.GroupData_Field.GroupMembershipCount = unpackInt(byteBuffer)
        this.GroupData_Field.GroupRolesCount = unpackInt(byteBuffer)
        this.GroupData_Field.AllowPublish = unpackBoolean(byteBuffer)
        this.GroupData_Field.MaturePublish = unpackBoolean(byteBuffer)
        this.GroupData_Field.OwnerRole = unpackUUID(byteBuffer)
    }
}
