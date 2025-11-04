package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class AcceptFriendship extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields = new ArrayList<>();
    public TransactionBlock TransactionBlock_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class FolderData {
        public UUID FolderID;
    }

    /* loaded from: classes.dex */
    public static class TransactionBlock {
        public UUID TransactionID;
    }

    public AcceptFriendship() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.TransactionBlock_Field = new TransactionBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.FolderData_Fields.size() * 16) + 53;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAcceptFriendship(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 41);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.TransactionBlock_Field.TransactionID);
        byteBuffer.put((byte) this.FolderData_Fields.size());
        Iterator<T> it = this.FolderData_Fields.iterator();
        while (it.hasNext()) {
            packUUID(byteBuffer, ((FolderData) it.next()).FolderID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.TransactionBlock_Field.TransactionID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            FolderData folderData = new FolderData();
            folderData.FolderID = unpackUUID(byteBuffer);
            this.FolderData_Fields.add(folderData);
        }
    }
}
