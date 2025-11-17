package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupAccountTransactionsReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<HistoryData> HistoryData_Fields = ArrayList<>()
    MoneyData MoneyData_Field

    class AgentData {
        UUID AgentID
        UUID GroupID
    }

    class HistoryData {
        Int Amount
        byte[] Item
        byte[] Time
        Int Type
        byte[] User
    }

    class MoneyData {
        Int CurrentInterval
        Int IntervalDays
        UUID RequestID
        byte[] StartDate
    }

    GroupAccountTransactionsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.MoneyData_Field = MoneyData()
    }

    Int CalcPayloadSize() {
        Int length = this.MoneyData_Field.StartDate.length + 25 + 36 + 1
        Iterator<T> it = this.HistoryData_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            HistoryData historyData = (HistoryData) it.next()
            length = historyData.Item.length + historyData.Time.length + 1 + 1 + historyData.User.length + 4 + 1 + 4 + i
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountTransactionsReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 102)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.MoneyData_Field.RequestID)
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays)
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval)
        packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1)
        byteBuffer.put((byte) this.HistoryData_Fields.size())
        for (HistoryData historyData : this.HistoryData_Fields) {
            packVariable(byteBuffer, historyData.Time, 1)
            packVariable(byteBuffer, historyData.User, 1)
            packInt(byteBuffer, historyData.Type)
            packVariable(byteBuffer, historyData.Item, 1)
            packInt(byteBuffer, historyData.Amount)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer)
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer)
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer)
        this.MoneyData_Field.StartDate = unpackVariable(byteBuffer, 1)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            HistoryData historyData = HistoryData()
            historyData.Time = unpackVariable(byteBuffer, 1)
            historyData.User = unpackVariable(byteBuffer, 1)
            historyData.Type = unpackInt(byteBuffer)
            historyData.Item = unpackVariable(byteBuffer, 1)
            historyData.Amount = unpackInt(byteBuffer)
            this.HistoryData_Fields.add(historyData)
        }
    }
}
