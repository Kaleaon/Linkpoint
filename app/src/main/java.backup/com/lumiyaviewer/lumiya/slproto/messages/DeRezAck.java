package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DeRezAck extends SLMessage {
    public TransactionData TransactionData_Field = new TransactionData();

    public static class TransactionData {
        public boolean Success;
        public UUID TransactionID;
    }

    public DeRezAck() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 21;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezAck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 36);
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        packBoolean(byteBuffer, this.TransactionData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer);
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer);
    }
}
