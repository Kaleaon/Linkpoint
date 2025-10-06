package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentRequestSit : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public TargetObject TargetObject_Field = TargetObject()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class TargetObject {
        public LLVector3 Offset
        public UUID TargetID
    }

    public AgentRequestSit() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 61
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentRequestSit(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.TargetObject_Field.TargetID)
        packLLVector3(byteBuffer, this.TargetObject_Field.Offset)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.TargetObject_Field.TargetID = unpackUUID(byteBuffer)
        this.TargetObject_Field.Offset = unpackLLVector3(byteBuffer)
    }
}
