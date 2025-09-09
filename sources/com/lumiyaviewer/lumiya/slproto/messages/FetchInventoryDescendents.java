package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FetchInventoryDescendents extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public InventoryData InventoryData_Field = new InventoryData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class InventoryData {
        public boolean FetchFolders;
        public boolean FetchItems;
        public UUID FolderID;
        public UUID OwnerID;
        public int SortOrder;
    }

    public FetchInventoryDescendents() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 74;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFetchInventoryDescendents(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.NAK);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.InventoryData_Field.FolderID);
        packUUID(byteBuffer, this.InventoryData_Field.OwnerID);
        packInt(byteBuffer, this.InventoryData_Field.SortOrder);
        packBoolean(byteBuffer, this.InventoryData_Field.FetchFolders);
        packBoolean(byteBuffer, this.InventoryData_Field.FetchItems);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.InventoryData_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryData_Field.OwnerID = unpackUUID(byteBuffer);
        this.InventoryData_Field.SortOrder = unpackInt(byteBuffer);
        this.InventoryData_Field.FetchFolders = unpackBoolean(byteBuffer);
        this.InventoryData_Field.FetchItems = unpackBoolean(byteBuffer);
    }
}
