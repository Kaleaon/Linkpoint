package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class AcceptCallingCard extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields = new ArrayList<>();
    public TransactionBlock TransactionBlock_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class FolderData {
        public UUID FolderID;
    }

    public static class TransactionBlock {
        public UUID TransactionID;
    }

    public AcceptCallingCard() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.TransactionBlock_Field = new TransactionBlock();
    }

    public int CalcPayloadSize() {
        return (this.FolderData_Fields.size() * 16) + 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAcceptCallingCard(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 46);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID);
        byteBuffer.put((byte) this.FolderData_Fields.size());
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            FolderData folderData = new FolderData();
            folderData.FolderID = unpackUUID(byteBuffer);
            this.FolderData_Fields.add(folderData);
        }
    }
}
