package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountSummaryRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MoneyData MoneyData_Field = MoneyData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class MoneyData {
        public Int CurrentInterval
        public Int IntervalDays
        public UUID RequestID
    }

    public GroupAccountSummaryRequest() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 76
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountSummaryRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 97)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.MoneyData_Field.RequestID)
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays)
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer)
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer)
    }
}
