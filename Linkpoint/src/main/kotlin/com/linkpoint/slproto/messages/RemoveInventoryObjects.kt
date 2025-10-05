package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class RemoveInventoryObjects : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<FolderData> FolderData_Fields = ArrayList<>()
    public ArrayList<ItemData> ItemData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class FolderData {
        public UUID FolderID
    }

    @JvmStatic
    class ItemData {
        public UUID ItemID
    }

    public RemoveInventoryObjects() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.FolderData_Fields.size() * 16) + 37 + 1 + (this.ItemData_Fields.size() * 16)
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveInventoryObjects(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.FS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.FolderData_Fields.size())
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID)
        }
        byteBuffer.put((Byte) this.ItemData_Fields.size())
        for (ItemData itemData : this.ItemData_Fields) {
            packUUID(byteBuffer, itemData.ItemID)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            FolderData folderData = FolderData()
            folderData.FolderID = unpackUUID(byteBuffer)
            this.FolderData_Fields.add(folderData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            ItemData itemData = ItemData()
            itemData.ItemID = unpackUUID(byteBuffer)
            this.ItemData_Fields.add(itemData)
        }
    }
}
