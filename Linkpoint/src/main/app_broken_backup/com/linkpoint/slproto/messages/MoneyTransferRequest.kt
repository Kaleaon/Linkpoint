package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyTransferRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    MoneyData MoneyData_Field = MoneyData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MoneyData {
        Int AggregatePermInventory
        Int AggregatePermNextOwner
        Int Amount
        ByteArray Description
        UUID DestID
        Int Flags
        UUID SourceID
        Int TransactionType
    }

    MoneyTransferRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.MoneyData_Field.Description.size + 44 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMoneyTransferRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 55)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.MoneyData_Field.SourceID)
        packUUID(byteBuffer, this.MoneyData_Field.DestID)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.Flags)
        packInt(byteBuffer, this.MoneyData_Field.Amount)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.AggregatePermNextOwner)
        packByte(byteBuffer, (this as Byte).MoneyData_Field.AggregatePermInventory)
        packInt(byteBuffer, this.MoneyData_Field.TransactionType)
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.SourceID = unpackUUID(byteBuffer)
        this.MoneyData_Field.DestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.Amount = unpackInt(byteBuffer)
        this.MoneyData_Field.AggregatePermNextOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.AggregatePermInventory = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MoneyData_Field.TransactionType = unpackInt(byteBuffer)
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1)
    }
}
