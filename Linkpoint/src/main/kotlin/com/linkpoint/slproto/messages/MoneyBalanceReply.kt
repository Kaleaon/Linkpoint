package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MoneyBalanceReply : SLMessage() {
    public MoneyData MoneyData_Field = MoneyData()
    public TransactionInfo TransactionInfo_Field = TransactionInfo()

    @JvmStatic
    class MoneyData {
        public UUID AgentID
        public Byte[] Description
        public Int MoneyBalance
        public Int SquareMetersCommitted
        public Int SquareMetersCredit
        public UUID TransactionID
        public Boolean TransactionSuccess
    }

    @JvmStatic
    class TransactionInfo {
        public Int Amount
        public UUID DestID
        public Boolean IsDestGroup
        public Boolean IsSourceGroup
        public Byte[] ItemDescription
        public UUID SourceID
        public Int TransactionType
    }

    public MoneyBalanceReply() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 46 + 4 + this.TransactionInfo_Field.ItemDescription.length + 43
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyBalanceReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 58)
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
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
