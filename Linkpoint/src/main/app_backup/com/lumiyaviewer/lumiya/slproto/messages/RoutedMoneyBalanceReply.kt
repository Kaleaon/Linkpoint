package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class RoutedMoneyBalanceReply : SLMessage {
    MoneyData MoneyData_Field = MoneyData()
    TargetBlock TargetBlock_Field = TargetBlock()
    TransactionInfo TransactionInfo_Field = TransactionInfo()

    class MoneyData {
        UUID AgentID
        ByteArray Description
        Int MoneyBalance
        Int SquareMetersCommitted
        Int SquareMetersCredit
        UUID TransactionID
        Boolean TransactionSuccess
    }

    class TargetBlock {
        Inet4Address TargetIP
        Int TargetPort
    }

    class TransactionInfo {
        Int Amount
        UUID DestID
        Boolean IsDestGroup
        Boolean IsSourceGroup
        ByteArray ItemDescription
        UUID SourceID
        Int TransactionType
    }

    RoutedMoneyBalanceReply() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 46 + 10 + this.TransactionInfo_Field.ItemDescription.length + 43
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRoutedMoneyBalanceReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 59)
        packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP)
        packShort(byteBuffer, (Short) this.TargetBlock_Field.TargetPort)
        packUUID(byteBuffer, this.MoneyData_Field.AgentID)
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID)
        packBoolean(byteBuffer, this.MoneyData_Field.TransactionSuccess)
        packInt(byteBuffer, this.MoneyData_Field.MoneyBalance)
        packInt(byteBuffer, this.MoneyData_Field.SquareMetersCredit)
        packInt(byteBuffer, this.MoneyData_Field.SquareMetersCommitted)
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1)
        packInt(byteBuffer, this.TransactionInfo_Field.TransactionType)
        packUUID(byteBuffer, this.TransactionInfo_Field.SourceID)
        packBoolean(byteBuffer, this.TransactionInfo_Field.IsSourceGroup)
        packUUID(byteBuffer, this.TransactionInfo_Field.DestID)
        packBoolean(byteBuffer, this.TransactionInfo_Field.IsDestGroup)
        packInt(byteBuffer, this.TransactionInfo_Field.Amount)
        packVariable(byteBuffer, this.TransactionInfo_Field.ItemDescription, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.TargetIP = unpackIPAddress(byteBuffer)
        this.TargetBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535
        this.MoneyData_Field.AgentID = unpackUUID(byteBuffer)
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer)
        this.MoneyData_Field.TransactionSuccess = unpackBoolean(byteBuffer)
        this.MoneyData_Field.MoneyBalance = unpackInt(byteBuffer)
        this.MoneyData_Field.SquareMetersCredit = unpackInt(byteBuffer)
        this.MoneyData_Field.SquareMetersCommitted = unpackInt(byteBuffer)
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1)
        this.TransactionInfo_Field.TransactionType = unpackInt(byteBuffer)
        this.TransactionInfo_Field.SourceID = unpackUUID(byteBuffer)
        this.TransactionInfo_Field.IsSourceGroup = unpackBoolean(byteBuffer)
        this.TransactionInfo_Field.DestID = unpackUUID(byteBuffer)
        this.TransactionInfo_Field.IsDestGroup = unpackBoolean(byteBuffer)
        this.TransactionInfo_Field.Amount = unpackInt(byteBuffer)
        this.TransactionInfo_Field.ItemDescription = unpackVariable(byteBuffer, 1)
    }
}
