package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class TransferInventoryAck extends SLMessage {
    public InfoBlock InfoBlock_Field;

    /* loaded from: classes.dex */
    public static class InfoBlock {
        public UUID InventoryID;
        public UUID TransactionID;
    }

    public TransferInventoryAck() {
        this.zeroCoded = true;
        this.InfoBlock_Field = new InfoBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferInventoryAck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 40);
        packUUID(byteBuffer, this.InfoBlock_Field.TransactionID);
        packUUID(byteBuffer, this.InfoBlock_Field.InventoryID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InfoBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.InfoBlock_Field.InventoryID = unpackUUID(byteBuffer);
    }
}
