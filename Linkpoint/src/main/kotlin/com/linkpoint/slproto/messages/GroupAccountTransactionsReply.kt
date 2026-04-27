package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupAccountTransactionsReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<HistoryData> HistoryData_Fields = ArrayList<>()
    public MoneyData MoneyData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
    }

    @JvmStatic
    class HistoryData {
        public Int Amount
        public ByteArray Item
        public ByteArray Time
        public Int Type
        public ByteArray User
    }

    @JvmStatic
    class MoneyData {
        public Int CurrentInterval
        public Int IntervalDays
        public UUID RequestID
        public ByteArray StartDate
    }

    public GroupAccountTransactionsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.MoneyData_Field = MoneyData()
    }

    public fun CalcPayloadSize(): Int {
        val length: Int = this.MoneyData_Field.StartDate.length + 25 + 36 + 1
        val it: Iterator<T> = this.HistoryData_Fields.iterator()
        while (true) {
            val i: Int = length
            if (!it.hasNext()) {
                return i
            }
            val historyData: HistoryData = (HistoryData) it.next()
            length = historyData.Item.length + historyData.Time.length + 1 + 1 + historyData.User.length + 4 + 1 + 4 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupAccountTransactionsReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 102)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.MoneyData_Field.RequestID)
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays)
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval)
        packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1)
        byteBuffer.put((Byte) this.HistoryData_Fields.size())
        for (HistoryData historyData : this.HistoryData_Fields) {
            packVariable(byteBuffer, historyData.Time, 1)
            packVariable(byteBuffer, historyData.User, 1)
            packInt(byteBuffer, historyData.Type)
            packVariable(byteBuffer, historyData.Item, 1)
            packInt(byteBuffer, historyData.Amount)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer)
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer)
        this.MoneyData_Field.StartDate = unpackVariable(byteBuffer, 1)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val historyData: HistoryData = HistoryData()
            historyData.Time = unpackVariable(byteBuffer, 1)
            historyData.User = unpackVariable(byteBuffer, 1)
            historyData.Type = unpackInt(byteBuffer)
            historyData.Item = unpackVariable(byteBuffer, 1)
            historyData.Amount = unpackInt(byteBuffer)
            this.HistoryData_Fields.add(historyData)
        }
    }
}
