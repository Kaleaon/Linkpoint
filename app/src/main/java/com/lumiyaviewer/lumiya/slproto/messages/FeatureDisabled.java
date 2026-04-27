package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FeatureDisabled extends SLMessage {
    public FailureInfo FailureInfo_Field = new FailureInfo();

    public static class FailureInfo {
        public UUID AgentID;
        public byte[] ErrorMessage;
        public UUID TransactionID;
    }

    public FeatureDisabled() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.FailureInfo_Field.ErrorMessage.length + 1 + 16 + 16 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFeatureDisabled(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 19);
        packVariable(byteBuffer, this.FailureInfo_Field.ErrorMessage, 1);
        packUUID(byteBuffer, this.FailureInfo_Field.AgentID);
        packUUID(byteBuffer, this.FailureInfo_Field.TransactionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.FailureInfo_Field.ErrorMessage = unpackVariable(byteBuffer, 1);
        this.FailureInfo_Field.AgentID = unpackUUID(byteBuffer);
        this.FailureInfo_Field.TransactionID = unpackUUID(byteBuffer);
    }
}
