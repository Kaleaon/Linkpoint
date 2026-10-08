package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class OfferCallingCard extends SLMessage {
    public AgentBlock AgentBlock_Field;
    public AgentData AgentData_Field;

    /* loaded from: classes.dex */
    public static class AgentBlock {
        public UUID DestID;
        public UUID TransactionID;
    }

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public OfferCallingCard() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.AgentBlock_Field = new AgentBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 68;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfferCallingCard(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 45);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentBlock_Field.DestID);
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer);
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer);
    }
}
