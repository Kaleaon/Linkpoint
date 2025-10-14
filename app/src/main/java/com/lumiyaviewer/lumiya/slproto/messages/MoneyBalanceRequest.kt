package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 52
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyBalanceRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 57)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
