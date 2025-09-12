package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class BuyObjectInventory extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public UUID FolderID;
        public UUID ItemID;
        public UUID ObjectID;
    }

    public BuyObjectInventory() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 84;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleBuyObjectInventory(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 103);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packUUID(byteBuffer, this.Data_Field.ItemID);
        packUUID(byteBuffer, this.Data_Field.FolderID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.ItemID = unpackUUID(byteBuffer);
        this.Data_Field.FolderID = unpackUUID(byteBuffer);
    }
}
