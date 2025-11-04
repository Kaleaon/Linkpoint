package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

/* loaded from: classes.dex */
public class TransferInventory extends SLMessage {
    public InfoBlock InfoBlock_Field;
    public ArrayList<InventoryBlock> InventoryBlock_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class InfoBlock {
        public UUID DestID;
        public UUID SourceID;
        public UUID TransactionID;
    }

    /* loaded from: classes.dex */
    public static class InventoryBlock {
        public UUID InventoryID;
        public int Type;
    }

    public TransferInventory() {
        this.zeroCoded = true;
        this.InfoBlock_Field = new InfoBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.InventoryBlock_Fields.size() * 17) + 53;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTransferInventory(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 39);
        packUUID(byteBuffer, this.InfoBlock_Field.SourceID);
        packUUID(byteBuffer, this.InfoBlock_Field.DestID);
        packUUID(byteBuffer, this.InfoBlock_Field.TransactionID);
        byteBuffer.put((byte) this.InventoryBlock_Fields.size());
        for (InventoryBlock inventoryBlock : this.InventoryBlock_Fields) {
            packUUID(byteBuffer, inventoryBlock.InventoryID);
            packByte(byteBuffer, (byte) inventoryBlock.Type);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InfoBlock_Field.SourceID = unpackUUID(byteBuffer);
        this.InfoBlock_Field.DestID = unpackUUID(byteBuffer);
        this.InfoBlock_Field.TransactionID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            InventoryBlock inventoryBlock = new InventoryBlock();
            inventoryBlock.InventoryID = unpackUUID(byteBuffer);
            inventoryBlock.Type = unpackByte(byteBuffer);
            this.InventoryBlock_Fields.add(inventoryBlock);
        }
    }
}
