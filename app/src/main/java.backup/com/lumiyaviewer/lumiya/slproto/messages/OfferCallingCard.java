package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class OfferCallingCard extends SLMessage {
    public AgentBlock AgentBlock_Field = new AgentBlock();
    public AgentData AgentData_Field = new AgentData();

    public static class AgentBlock {
        public UUID DestID;
        public UUID TransactionID;
    }

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public OfferCallingCard() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 68;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfferCallingCard(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 45);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentBlock_Field.DestID);
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer);
    }
}
