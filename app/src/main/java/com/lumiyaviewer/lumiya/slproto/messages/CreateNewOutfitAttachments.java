package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class CreateNewOutfitAttachments extends SLMessage {
    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class HeaderData {
        public UUID NewFolderID;
    }

    public static class ObjectData {
        public UUID OldFolderID;
        public UUID OldItemID;
    }

    public CreateNewOutfitAttachments() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.HeaderData_Field = new HeaderData();
    }

    public int CalcPayloadSize() {
        return (this.ObjectData_Fields.size() * 32) + 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateNewOutfitAttachments(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -114);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.HeaderData_Field.NewFolderID);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.OldItemID);
            packUUID(byteBuffer, objectData.OldFolderID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.HeaderData_Field.NewFolderID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.OldItemID = unpackUUID(byteBuffer);
            objectData.OldFolderID = unpackUUID(byteBuffer);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
