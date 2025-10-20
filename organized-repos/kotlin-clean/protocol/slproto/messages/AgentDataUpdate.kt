package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentDataUpdate : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID ActiveGroupID
        public UUID AgentID
        public Byte[] FirstName
        public Byte[] GroupName
        public Long GroupPowers
        public Byte[] GroupTitle
        public Byte[] LastName
    }

    public AgentDataUpdate() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.AgentData_Field.FirstName.length + 17 + 1 + this.AgentData_Field.LastName.length + 1 + this.AgentData_Field.GroupTitle.length + 16 + 8 + 1 + this.AgentData_Field.GroupName.length + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentDataUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -125)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packVariable(byteBuffer, this.AgentData_Field.FirstName, 1)
        packVariable(byteBuffer, this.AgentData_Field.LastName, 1)
        packVariable(byteBuffer, this.AgentData_Field.GroupTitle, 1)
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID)
        packLong(byteBuffer, this.AgentData_Field.GroupPowers)
        packVariable(byteBuffer, this.AgentData_Field.GroupName, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.FirstName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.LastName = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.GroupTitle = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupPowers = unpackLong(byteBuffer)
        this.AgentData_Field.GroupName = unpackVariable(byteBuffer, 1)
    }
}
