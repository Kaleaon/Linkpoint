package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class DeRezAck extends SLMessage {
    public TransactionData TransactionData_Field;

    /* loaded from: classes.dex */
    public static class TransactionData {
        public boolean Success;
        public UUID TransactionID;
    }

    public DeRezAck() {
        this.zeroCoded = false;
        this.TransactionData_Field = new TransactionData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 21;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDeRezAck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 36);
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        packBoolean(byteBuffer, this.TransactionData_Field.Success);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer);
        this.TransactionData_Field.Success = unpackBoolean(byteBuffer);
    }
}
