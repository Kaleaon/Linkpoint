package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentThrottle : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Throttle Throttle_Field = Throttle()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Int CircuitCode
        public UUID SessionID
    }

    @JvmStatic
    class Throttle {
        public Int GenCounter
        public ByteArray Throttles
    }

    public AgentThrottle() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.Throttle_Field.Throttles.length + 5 + 40
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentThrottle(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 81)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.Throttle_Field.GenCounter)
        packVariable(byteBuffer, this.Throttle_Field.Throttles, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.Throttle_Field.GenCounter = unpackInt(byteBuffer)
        this.Throttle_Field.Throttles = unpackVariable(byteBuffer, 1)
    }
}
