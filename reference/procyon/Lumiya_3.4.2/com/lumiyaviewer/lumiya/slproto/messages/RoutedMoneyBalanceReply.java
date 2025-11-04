// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RoutedMoneyBalanceReply extends SLMessage
{
    public MoneyData MoneyData_Field;
    public TargetBlock TargetBlock_Field;
    public TransactionInfo TransactionInfo_Field;
    
    public RoutedMoneyBalanceReply() {
        this.zeroCoded = true;
        this.TargetBlock_Field = new TargetBlock();
        this.MoneyData_Field = new MoneyData();
        this.TransactionInfo_Field = new TransactionInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MoneyData_Field.Description.length + 46 + 10 + (this.TransactionInfo_Field.ItemDescription.length + 43);
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRoutedMoneyBalanceReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)59);
        this.packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP);
        this.packShort(byteBuffer, (short)this.TargetBlock_Field.TargetPort);
        this.packUUID(byteBuffer, this.MoneyData_Field.AgentID);
        this.packUUID(byteBuffer, this.MoneyData_Field.TransactionID);
        this.packBoolean(byteBuffer, this.MoneyData_Field.TransactionSuccess);
        this.packInt(byteBuffer, this.MoneyData_Field.MoneyBalance);
        this.packInt(byteBuffer, this.MoneyData_Field.SquareMetersCredit);
        this.packInt(byteBuffer, this.MoneyData_Field.SquareMetersCommitted);
        this.packVariable(byteBuffer, this.MoneyData_Field.Description, 1);
        this.packInt(byteBuffer, this.TransactionInfo_Field.TransactionType);
        this.packUUID(byteBuffer, this.TransactionInfo_Field.SourceID);
        this.packBoolean(byteBuffer, this.TransactionInfo_Field.IsSourceGroup);
        this.packUUID(byteBuffer, this.TransactionInfo_Field.DestID);
        this.packBoolean(byteBuffer, this.TransactionInfo_Field.IsDestGroup);
        this.packInt(byteBuffer, this.TransactionInfo_Field.Amount);
        this.packVariable(byteBuffer, this.TransactionInfo_Field.ItemDescription, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TargetBlock_Field.TargetIP = this.unpackIPAddress(byteBuffer);
        this.TargetBlock_Field.TargetPort = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.MoneyData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionSuccess = this.unpackBoolean(byteBuffer);
        this.MoneyData_Field.MoneyBalance = this.unpackInt(byteBuffer);
        this.MoneyData_Field.SquareMetersCredit = this.unpackInt(byteBuffer);
        this.MoneyData_Field.SquareMetersCommitted = this.unpackInt(byteBuffer);
        this.MoneyData_Field.Description = this.unpackVariable(byteBuffer, 1);
        this.TransactionInfo_Field.TransactionType = this.unpackInt(byteBuffer);
        this.TransactionInfo_Field.SourceID = this.unpackUUID(byteBuffer);
        this.TransactionInfo_Field.IsSourceGroup = this.unpackBoolean(byteBuffer);
        this.TransactionInfo_Field.DestID = this.unpackUUID(byteBuffer);
        this.TransactionInfo_Field.IsDestGroup = this.unpackBoolean(byteBuffer);
        this.TransactionInfo_Field.Amount = this.unpackInt(byteBuffer);
        this.TransactionInfo_Field.ItemDescription = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class MoneyData
    {
        public UUID AgentID;
        public byte[] Description;
        public int MoneyBalance;
        public int SquareMetersCommitted;
        public int SquareMetersCredit;
        public UUID TransactionID;
        public boolean TransactionSuccess;
    }
    
    public static class TargetBlock
    {
        public Inet4Address TargetIP;
        public int TargetPort;
    }
    
    public static class TransactionInfo
    {
        public int Amount;
        public UUID DestID;
        public boolean IsDestGroup;
        public boolean IsSourceGroup;
        public byte[] ItemDescription;
        public UUID SourceID;
        public int TransactionType;
    }
}
