package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentDataUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID ActiveGroupID
        UUID AgentID
        ByteArray FirstName
        ByteArray GroupName
        Long GroupPowers
        ByteArray GroupTitle
        ByteArray LastName
    }

    AgentDataUpdate() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.AgentData_Field.FirstName.size + 17 + 1 + this.AgentData_Field.LastName.size + 1 + this.AgentData_Field.GroupTitle.size + 16 + 8 + 1 + this.AgentData_Field.GroupName.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAgentDataUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -125)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packVariable(byteBuffer, this.AgentData_Field.FirstName, 1)
        packVariable(byteBuffer, this.AgentData_Field.LastName, 1)
        packVariable(byteBuffer, this.AgentData_Field.GroupTitle, 1)
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID)
        packLong(byteBuffer, this.AgentData_Field.GroupPowers)
        packVariable(byteBuffer, this.AgentData_Field.GroupName, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.FirstName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.LastName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.GroupTitle = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupPowers = unpackLong(byteBuffer)
        this.AgentData_Field.GroupName = unpackVariable(byteBuffer, 1)
    }
}
