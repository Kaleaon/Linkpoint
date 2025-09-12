package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ReplyTaskInventory extends SLMessage {
    public InventoryData InventoryData_Field = new InventoryData();

    public static class InventoryData {
        public byte[] Filename;
        public int Serial;
        public UUID TaskID;
    }

    public ReplyTaskInventory() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.InventoryData_Field.Filename.length + 19 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleReplyTaskInventory(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 34);
        packUUID(byteBuffer, this.InventoryData_Field.TaskID);
        packShort(byteBuffer, (short) this.InventoryData_Field.Serial);
        packVariable(byteBuffer, this.InventoryData_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.InventoryData_Field.TaskID = unpackUUID(byteBuffer);
        this.InventoryData_Field.Serial = unpackShort(byteBuffer);
        this.InventoryData_Field.Filename = unpackVariable(byteBuffer, 1);
    }
}
