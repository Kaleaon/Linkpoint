package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentAlertMessage : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public AlertData AlertData_Field = AlertData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class AlertData {
        public Byte[] Message
        public Boolean Modal
    }

    public AgentAlertMessage() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.AlertData_Field.Message.length + 2 + 20
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentAlertMessage(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -121)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packBoolean(byteBuffer, this.AlertData_Field.Modal)
        packVariable(byteBuffer, this.AlertData_Field.Message, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AlertData_Field.Modal = unpackBoolean(byteBuffer)
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
