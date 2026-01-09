package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateGroupRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GroupData {
        Boolean AllowPublish
        ByteArray Charter
        UUID InsigniaID
        Boolean MaturePublish
        Int MembershipFee
        ByteArray Name
        Boolean OpenEnrollment
        Boolean ShowInList
    }

    CreateGroupRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.GroupData_Field.Name.size + 1 + 2 + this.GroupData_Field.Charter.size + 1 + 16 + 4 + 1 + 1 + 1 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCreateGroupRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 83)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.GroupData_Field.Name, 1)
        packVariable(byteBuffer, this.GroupData_Field.Charter, 2)
        packBoolean(byteBuffer, this.GroupData_Field.ShowInList)
        packUUID(byteBuffer, this.GroupData_Field.InsigniaID)
        packInt(byteBuffer, this.GroupData_Field.MembershipFee)
        packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment)
        packBoolean(byteBuffer, this.GroupData_Field.AllowPublish)
        packBoolean(byteBuffer, this.GroupData_Field.MaturePublish)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.Name = unpackVariable(byteBuffer, 1)
        this.GroupData_Field.Charter = unpackVariable(byteBuffer, 2)
        this.GroupData_Field.ShowInList = unpackBoolean(byteBuffer)
        this.GroupData_Field.InsigniaID = unpackUUID(byteBuffer)
        this.GroupData_Field.MembershipFee = unpackInt(byteBuffer)
        this.GroupData_Field.OpenEnrollment = unpackBoolean(byteBuffer)
        this.GroupData_Field.AllowPublish = unpackBoolean(byteBuffer)
        this.GroupData_Field.MaturePublish = unpackBoolean(byteBuffer)
    }
}
