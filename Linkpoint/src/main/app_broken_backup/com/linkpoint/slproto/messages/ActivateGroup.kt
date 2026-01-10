package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ActivateGroup : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    constructor() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(sLMessageHandler: SLMessageHandler)  {
        sLMessageHandler.HandleActivateGroup(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 112)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
    }
}
