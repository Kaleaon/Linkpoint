// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupAccountTransactionsReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<HistoryData> HistoryData_Fields;
    public MoneyData MoneyData_Field;
    
    public GroupAccountTransactionsReply() {
        this.HistoryData_Fields = new ArrayList<HistoryData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.MoneyData_Field.StartDate.length;
        final Iterator<Object> iterator = this.HistoryData_Fields.iterator();
        int n = length + 25 + 36 + 1;
        while (iterator.hasNext()) {
            final HistoryData historyData = iterator.next();
            n += historyData.Item.length + (historyData.Time.length + 1 + 1 + historyData.User.length + 4 + 1) + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupAccountTransactionsReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)102);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        this.packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        this.packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
        this.packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1);
        byteBuffer.put((byte)this.HistoryData_Fields.size());
        for (final HistoryData historyData : this.HistoryData_Fields) {
            this.packVariable(byteBuffer, historyData.Time, 1);
            this.packVariable(byteBuffer, historyData.User, 1);
            this.packInt(byteBuffer, historyData.Type);
            this.packVariable(byteBuffer, historyData.Item, 1);
            this.packInt(byteBuffer, historyData.Amount);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = this.unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = this.unpackInt(byteBuffer);
        this.MoneyData_Field.StartDate = this.unpackVariable(byteBuffer, 1);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final HistoryData e = new HistoryData();
            e.Time = this.unpackVariable(byteBuffer, 1);
            e.User = this.unpackVariable(byteBuffer, 1);
            e.Type = this.unpackInt(byteBuffer);
            e.Item = this.unpackVariable(byteBuffer, 1);
            e.Amount = this.unpackInt(byteBuffer);
            this.HistoryData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
    }
    
    public static class HistoryData
    {
        public int Amount;
        public byte[] Item;
        public byte[] Time;
        public int Type;
        public byte[] User;
    }
    
    public static class MoneyData
    {
        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;
        public byte[] StartDate;
    }
}
