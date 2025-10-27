package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupAccountSummaryReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MoneyData MoneyData_Field = MoneyData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
    }

    @JvmStatic
    class MoneyData {
        public Int Balance
        public Int CurrentInterval
        public Int GroupTaxCurrent
        public Int GroupTaxEstimate
        public Int IntervalDays
        public Int LandTaxCurrent
        public Int LandTaxEstimate
        public ByteArray LastTaxDate
        public Int LightTaxCurrent
        public Int LightTaxEstimate
        public Int NonExemptMembers
        public Int ObjectTaxCurrent
        public Int ObjectTaxEstimate
        public Int ParcelDirFeeCurrent
        public Int ParcelDirFeeEstimate
        public UUID RequestID
        public ByteArray StartDate
        public ByteArray TaxDate
        public Int TotalCredits
        public Int TotalDebits
    }

    public GroupAccountSummaryReply() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.MoneyData_Field.StartDate.length + 25 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.MoneyData_Field.LastTaxDate.length + 1 + this.MoneyData_Field.TaxDate.length + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupAccountSummaryReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 98)
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
