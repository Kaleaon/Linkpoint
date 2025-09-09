package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class MoveInventoryFolder extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
        public boolean Stamp;
    }

    public static class InventoryData {
        public UUID FolderID;
        public UUID ParentID;
    }

    public MoveInventoryFolder() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 32) + 38;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveInventoryFolder(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 19);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.AgentData_Field.Stamp);
        byteBuffer.put((byte) this.InventoryData_Fields.size());
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.FolderID);
            packUUID(byteBuffer, inventoryData.ParentID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Stamp = unpackBoolean(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            InventoryData inventoryData = new InventoryData();
            inventoryData.FolderID = unpackUUID(byteBuffer);
            inventoryData.ParentID = unpackUUID(byteBuffer);
            this.InventoryData_Fields.add(inventoryData);
        }
    }
}
