package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountSummaryReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    MoneyData MoneyData_Field = MoneyData()

    class AgentData {
        UUID AgentID
        UUID GroupID
    }

    class MoneyData {
        Int Balance
        Int CurrentInterval
        Int GroupTaxCurrent
        Int GroupTaxEstimate
        Int IntervalDays
        Int LandTaxCurrent
        Int LandTaxEstimate
        byte[] LastTaxDate
        Int LightTaxCurrent
        Int LightTaxEstimate
        Int NonExemptMembers
        Int ObjectTaxCurrent
        Int ObjectTaxEstimate
        Int ParcelDirFeeCurrent
        Int ParcelDirFeeEstimate
        UUID RequestID
        byte[] StartDate
        byte[] TaxDate
        Int TotalCredits
        Int TotalDebits
    }

    GroupAccountSummaryReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.MoneyData_Field.StartDate.length + 25 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.MoneyData_Field.LastTaxDate.length + 1 + this.MoneyData_Field.TaxDate.length + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountSummaryReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 98)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.MoneyData_Field.RequestID)
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays)
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval)
        packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1)
        packInt(byteBuffer, this.MoneyData_Field.Balance)
        packInt(byteBuffer, this.MoneyData_Field.TotalCredits)
        packInt(byteBuffer, this.MoneyData_Field.TotalDebits)
        packInt(byteBuffer, this.MoneyData_Field.ObjectTaxCurrent)
        packInt(byteBuffer, this.MoneyData_Field.LightTaxCurrent)
        packInt(byteBuffer, this.MoneyData_Field.LandTaxCurrent)
        packInt(byteBuffer, this.MoneyData_Field.GroupTaxCurrent)
        packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeCurrent)
        packInt(byteBuffer, this.MoneyData_Field.ObjectTaxEstimate)
        packInt(byteBuffer, this.MoneyData_Field.LightTaxEstimate)
        packInt(byteBuffer, this.MoneyData_Field.LandTaxEstimate)
        packInt(byteBuffer, this.MoneyData_Field.GroupTaxEstimate)
        packInt(byteBuffer, this.MoneyData_Field.ParcelDirFeeEstimate)
        packInt(byteBuffer, this.MoneyData_Field.NonExemptMembers)
        packVariable(byteBuffer, this.MoneyData_Field.LastTaxDate, 1)
        packVariable(byteBuffer, this.MoneyData_Field.TaxDate, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer)
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer)
        this.MoneyData_Field.StartDate = unpackVariable(byteBuffer, 1)
        this.MoneyData_Field.Balance = unpackInt(byteBuffer)
        this.MoneyData_Field.TotalCredits = unpackInt(byteBuffer)
        this.MoneyData_Field.TotalDebits = unpackInt(byteBuffer)
        this.MoneyData_Field.ObjectTaxCurrent = unpackInt(byteBuffer)
        this.MoneyData_Field.LightTaxCurrent = unpackInt(byteBuffer)
        this.MoneyData_Field.LandTaxCurrent = unpackInt(byteBuffer)
        this.MoneyData_Field.GroupTaxCurrent = unpackInt(byteBuffer)
        this.MoneyData_Field.ParcelDirFeeCurrent = unpackInt(byteBuffer)
        this.MoneyData_Field.ObjectTaxEstimate = unpackInt(byteBuffer)
        this.MoneyData_Field.LightTaxEstimate = unpackInt(byteBuffer)
        this.MoneyData_Field.LandTaxEstimate = unpackInt(byteBuffer)
        this.MoneyData_Field.GroupTaxEstimate = unpackInt(byteBuffer)
        this.MoneyData_Field.ParcelDirFeeEstimate = unpackInt(byteBuffer)
        this.MoneyData_Field.NonExemptMembers = unpackInt(byteBuffer)
        this.MoneyData_Field.LastTaxDate = unpackVariable(byteBuffer, 1)
        this.MoneyData_Field.TaxDate = unpackVariable(byteBuffer, 1)
    }
}
