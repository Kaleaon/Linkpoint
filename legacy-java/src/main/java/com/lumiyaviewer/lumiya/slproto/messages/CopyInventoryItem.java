package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class CopyInventoryItem extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class InventoryData {
        public int CallbackID;
        public UUID NewFolderID;
        public byte[] NewName;
        public UUID OldAgentID;
        public UUID OldItemID;
    }

    public CopyInventoryItem() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 37;
        Iterator<T> it = this.InventoryData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((InventoryData) it.next()).NewName.length + 53 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCopyInventoryItem(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.CR);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.InventoryData_Fields.size());
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packInt(byteBuffer, inventoryData.CallbackID);
            packUUID(byteBuffer, inventoryData.OldAgentID);
            packUUID(byteBuffer, inventoryData.OldItemID);
            packUUID(byteBuffer, inventoryData.NewFolderID);
            packVariable(byteBuffer, inventoryData.NewName, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            InventoryData inventoryData = new InventoryData();
            inventoryData.CallbackID = unpackInt(byteBuffer);
            inventoryData.OldAgentID = unpackUUID(byteBuffer);
            inventoryData.OldItemID = unpackUUID(byteBuffer);
            inventoryData.NewFolderID = unpackUUID(byteBuffer);
            inventoryData.NewName = unpackVariable(byteBuffer, 1);
            this.InventoryData_Fields.add(inventoryData);
        }
    }
}
