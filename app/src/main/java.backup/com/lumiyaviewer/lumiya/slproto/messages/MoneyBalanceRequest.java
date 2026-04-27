package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MoneyBalanceRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public MoneyData MoneyData_Field = new MoneyData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class MoneyData {
        public UUID TransactionID;
    }

    public MoneyBalanceRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoneyBalanceRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 57);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.MoneyData_Field.TransactionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.MoneyData_Field.TransactionID = unpackUUID(byteBuffer);
    }
}
