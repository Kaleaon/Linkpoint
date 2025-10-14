package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentDataUpdate : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID ActiveGroupID
        UUID AgentID
        byte[] FirstName
        byte[] GroupName
        Long GroupPowers
        byte[] GroupTitle
        byte[] LastName
    }

    AgentDataUpdate() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.AgentData_Field.FirstName.length + 17 + 1 + this.AgentData_Field.LastName.length + 1 + this.AgentData_Field.GroupTitle.length + 16 + 8 + 1 + this.AgentData_Field.GroupName.length + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentDataUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.FirstName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.LastName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.GroupTitle = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupPowers = unpackLong(byteBuffer)
        this.AgentData_Field.GroupName = unpackVariable(byteBuffer, 1)
    }
}
