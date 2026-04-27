package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MoneyBalanceReply extends SLMessage {
    public MoneyData MoneyData_Field = new MoneyData();
    public TransactionInfo TransactionInfo_Field = new TransactionInfo();

    public static class MoneyData {
        public UUID AgentID;
        public byte[] Description;
        public int MoneyBalance;
        public int SquareMetersCommitted;
        public int SquareMetersCredit;
        public UUID TransactionID;
        public boolean TransactionSuccess;
    }

    public static class TransactionInfo {
        public int Amount;
        public UUID DestID;
        public boolean IsDestGroup;
        public boolean IsSourceGroup;
        public byte[] ItemDescription;
        public UUID SourceID;
        public int TransactionType;
    }

    public MoneyBalanceReply() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 46 + 4 + this.TransactionInfo_Field.ItemDescription.length + 43;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyBalanceReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 58);
        packUUID(byteBuffer, this.MoneyData_Field.AgentID);
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID);
        packBoolean(byteBuffer, this.MoneyData_Field.TransactionSuccess);
        packInt(byteBuffer, this.MoneyData_Field.MoneyBalance);
        packInt(byteBuffer, this.MoneyData_Field.SquareMetersCredit);
        packInt(byteBuffer, this.MoneyData_Field.SquareMetersCommitted);
        packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
        packInt(byteBuffer, this.TransactionInfo_Field.TransactionType);
        packUUID(byteBuffer, this.TransactionInfo_Field.SourceID);
        packBoolean(byteBuffer, this.TransactionInfo_Field.IsSourceGroup);
        packUUID(byteBuffer, this.TransactionInfo_Field.DestID);
        packBoolean(byteBuffer, this.TransactionInfo_Field.IsDestGroup);
        packInt(byteBuffer, this.TransactionInfo_Field.Amount);
        packVariable(byteBuffer, this.TransactionInfo_Field.ItemDescription, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.MoneyData_Field.AgentID = unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionSuccess = unpackBoolean(byteBuffer);
        this.MoneyData_Field.MoneyBalance = unpackInt(byteBuffer);
        this.MoneyData_Field.SquareMetersCredit = unpackInt(byteBuffer);
        this.MoneyData_Field.SquareMetersCommitted = unpackInt(byteBuffer);
        this.MoneyData_Field.Description = unpackVariable(byteBuffer, 1);
        this.TransactionInfo_Field.TransactionType = unpackInt(byteBuffer);
        this.TransactionInfo_Field.SourceID = unpackUUID(byteBuffer);
        this.TransactionInfo_Field.IsSourceGroup = unpackBoolean(byteBuffer);
        this.TransactionInfo_Field.DestID = unpackUUID(byteBuffer);
        this.TransactionInfo_Field.IsDestGroup = unpackBoolean(byteBuffer);
        this.TransactionInfo_Field.Amount = unpackInt(byteBuffer);
        this.TransactionInfo_Field.ItemDescription = unpackVariable(byteBuffer, 1);
    }
}
