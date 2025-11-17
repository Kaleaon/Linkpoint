package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentRequestSit : SLMessage {
    AgentData AgentData_Field = AgentData()
    TargetObject TargetObject_Field = TargetObject()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class TargetObject {
        LLVector3 Offset
        UUID TargetID
    }

    AgentRequestSit() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 61
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentRequestSit(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TargetObject_Field.TargetID)
        packLLVector3(byteBuffer, this.TargetObject_Field.Offset)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TargetObject_Field.TargetID = unpackUUID(byteBuffer)
        this.TargetObject_Field.Offset = unpackLLVector3(byteBuffer)
    }
}
