package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GroupAccountTransactionsRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MoneyData MoneyData_Field = new MoneyData();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class MoneyData {
        public int CurrentInterval;
        public int IntervalDays;
        public UUID RequestID;
    }

    public GroupAccountTransactionsRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 76;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupAccountTransactionsRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 101);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packUUID(byteBuffer, this.MoneyData_Field.RequestID);
        packInt(byteBuffer, this.MoneyData_Field.IntervalDays);
        packInt(byteBuffer, this.MoneyData_Field.CurrentInterval);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.MoneyData_Field.RequestID = unpackUUID(byteBuffer);
        this.MoneyData_Field.IntervalDays = unpackInt(byteBuffer);
        this.MoneyData_Field.CurrentInterval = unpackInt(byteBuffer);
    }
}
