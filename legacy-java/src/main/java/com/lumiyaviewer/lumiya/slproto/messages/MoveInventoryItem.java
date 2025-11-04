package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class MoveInventoryItem extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
        public boolean Stamp;
    }

    /* loaded from: classes.dex */
    public static class InventoryData {
        public UUID FolderID;
        public UUID ItemID;
        public byte[] NewName;
    }

    public MoveInventoryItem() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 38;
        Iterator<T> it = this.InventoryData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((InventoryData) it.next()).NewName.length + 33 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveInventoryItem(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.FF);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.AgentData_Field.Stamp);
        byteBuffer.put((byte) this.InventoryData_Fields.size());
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID);
            packUUID(byteBuffer, inventoryData.FolderID);
            packVariable(byteBuffer, inventoryData.NewName, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.Stamp = unpackBoolean(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            InventoryData inventoryData = new InventoryData();
            inventoryData.ItemID = unpackUUID(byteBuffer);
            inventoryData.FolderID = unpackUUID(byteBuffer);
            inventoryData.NewName = unpackVariable(byteBuffer, 1);
            this.InventoryData_Fields.add(inventoryData);
        }
    }
}
