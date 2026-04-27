package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TransferInventoryAck extends SLMessage {
    public InfoBlock InfoBlock_Field = new InfoBlock();

    public static class InfoBlock {
        public UUID InventoryID;
        public UUID TransactionID;
    }

    public TransferInventoryAck() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferInventoryAck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 40);
        packUUID(byteBuffer, this.InfoBlock_Field.TransactionID);
        packUUID(byteBuffer, this.InfoBlock_Field.InventoryID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InfoBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.InfoBlock_Field.InventoryID = unpackUUID(byteBuffer);
    }
}
