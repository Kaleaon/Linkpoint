package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class CreateLandmarkForEvent extends SLMessage {
    public AgentData AgentData_Field;
    public EventData EventData_Field;
    public InventoryBlock InventoryBlock_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class EventData {
        public int EventID;
    }

    /* loaded from: classes.dex */
    public static class InventoryBlock {
        public UUID FolderID;
        public byte[] Name;
    }

    public CreateLandmarkForEvent() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.EventData_Field = new EventData();
        this.InventoryBlock_Field = new InventoryBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.InventoryBlock_Field.Name.length + 17 + 40;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateLandmarkForEvent(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 50);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.EventData_Field.EventID);
        packUUID(byteBuffer, this.InventoryBlock_Field.FolderID);
        packVariable(byteBuffer, this.InventoryBlock_Field.Name, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.EventData_Field.EventID = unpackInt(byteBuffer);
        this.InventoryBlock_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryBlock_Field.Name = unpackVariable(byteBuffer, 1);
    }
}
