package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class MoveTaskInventory extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public InventoryData InventoryData_Field = new InventoryData();

    public static class AgentData {
        public UUID AgentID;
        public UUID FolderID;
        public UUID SessionID;
    }

    public static class InventoryData {
        public UUID ItemID;
        public int LocalID;
    }

    public MoveTaskInventory() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveTaskInventory(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 32);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.FolderID);
        packInt(byteBuffer, this.InventoryData_Field.LocalID);
        packUUID(byteBuffer, this.InventoryData_Field.ItemID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryData_Field.LocalID = unpackInt(byteBuffer);
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer);
    }
}
