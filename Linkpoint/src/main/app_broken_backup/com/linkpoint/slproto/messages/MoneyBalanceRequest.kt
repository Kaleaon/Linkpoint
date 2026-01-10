package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyBalanceRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    MoneyData MoneyData_Field = MoneyData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MoneyData {
        UUID TransactionID
    }

    MoneyBalanceRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMoneyBalanceRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 57)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
