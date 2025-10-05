package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentWearablesRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    public AgentWearablesRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentWearablesRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 125)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
    }
}
