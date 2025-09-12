package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class GroupAccountDetailsReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<HistoryData> HistoryData_Fields = new ArrayList<>();
    public MoneyData MoneyData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
    }

    public static class HistoryData {
        public int Amount;
        public byte[] Description;
    }

    public static class MoneyData {
        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;
        public byte[] StartDate;
    }

    public GroupAccountDetailsReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize() {
        int length = this.MoneyData_Field.StartDate.length + 25 + 36 + 1;
        Iterator<T> it = this.HistoryData_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i;
            }
            length = ((HistoryData) it.next()).Description.length + 1 + 4 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountDetailsReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 100);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
        packVariable(byteBuffer, this.MoneyData_Field.StartDate, 1);
        byteBuffer.put((byte) this.HistoryData_Fields.size());
        for (HistoryData historyData : this.HistoryData_Fields) {
            packVariable(byteBuffer, historyData.Description, 1);
            packInt(byteBuffer, historyData.Amount);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer);
        this.MoneyData_Field.StartDate = unpackVariable(byteBuffer, 1);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            HistoryData historyData = new HistoryData();
            historyData.Description = unpackVariable(byteBuffer, 1);
            historyData.Amount = unpackInt(byteBuffer);
            this.HistoryData_Fields.add(historyData);
        }
    }
}
