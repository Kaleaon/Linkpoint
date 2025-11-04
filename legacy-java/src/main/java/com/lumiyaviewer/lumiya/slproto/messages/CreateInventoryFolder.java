package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class CreateInventoryFolder extends SLMessage {
    public AgentData AgentData_Field;
    public FolderData FolderData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class FolderData {
        public UUID FolderID;
        public byte[] Name;
        public UUID ParentID;
        public int Type;
    }

    public CreateInventoryFolder() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FolderData_Field = new FolderData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.FolderData_Field.Name.length + 34 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateInventoryFolder(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 17);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.FolderData_Field.FolderID);
        packUUID(byteBuffer, this.FolderData_Field.ParentID);
        packByte(byteBuffer, (byte) this.FolderData_Field.Type);
        packVariable(byteBuffer, this.FolderData_Field.Name, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.FolderData_Field.FolderID = unpackUUID(byteBuffer);
        this.FolderData_Field.ParentID = unpackUUID(byteBuffer);
        this.FolderData_Field.Type = unpackByte(byteBuffer);
        this.FolderData_Field.Name = unpackVariable(byteBuffer, 1);
    }
}
