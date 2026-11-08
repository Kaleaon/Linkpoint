package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RoutedMoneyBalanceReply extends SLMessage {
    public MoneyData MoneyData_Field;
    public TargetBlock TargetBlock_Field;
    public TransactionInfo TransactionInfo_Field;

    /* loaded from: classes.dex */
    public static class MoneyData {
        public UUID AgentID;
        public byte[] Description;
        public int MoneyBalance;
        public int SquareMetersCommitted;
        public int SquareMetersCredit;
        public UUID TransactionID;
        public boolean TransactionSuccess;
    }

    /* loaded from: classes.dex */
    public static class TargetBlock {
        public Inet4Address TargetIP;
        public int TargetPort;
    }

    /* loaded from: classes.dex */
    public static class TransactionInfo {
        public int Amount;
        public UUID DestID;
        public boolean IsDestGroup;
        public boolean IsSourceGroup;
        public byte[] ItemDescription;
        public UUID SourceID;
        public int TransactionType;
    }

    public RoutedMoneyBalanceReply() {
        this.zeroCoded = true;
        this.TargetBlock_Field = new TargetBlock();
        this.MoneyData_Field = new MoneyData();
        this.TransactionInfo_Field = new TransactionInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 46 + 10 + this.TransactionInfo_Field.ItemDescription.length + 43;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRoutedMoneyBalanceReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 59);
        packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP);
        packShort(byteBuffer, (short) this.TargetBlock_Field.TargetPort);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.TargetIP = unpackIPAddress(byteBuffer);
        this.TargetBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535;
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
