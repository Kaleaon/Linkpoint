package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountSummaryRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    MoneyData MoneyData_Field = MoneyData()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class MoneyData {
        Int CurrentInterval
        Int IntervalDays
        UUID RequestID
    }

    GroupAccountSummaryRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 76
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupAccountSummaryRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 97)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.MoneyData_Field.RequestID)
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays)
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer)
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer)
    }
}
