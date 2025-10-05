package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CopyInventoryFromNotecard : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()
    public NotecardData NotecardData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public UUID FolderID
        public UUID ItemID
    }

    @JvmStatic
    class NotecardData {
        public UUID NotecardItemID
        public UUID ObjectID
    }

    public CopyInventoryFromNotecard() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.NotecardData_Field = NotecardData()
    }

    public Int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 32) + 69
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCopyInventoryFromNotecard(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 9)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.NotecardData_Field.NotecardItemID)
        packUUID(byteBuffer, this.NotecardData_Field.ObjectID)
        byteBuffer.put((Byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
            packUUID(byteBuffer, inventoryData.FolderID)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.NotecardData_Field.NotecardItemID = unpackUUID(byteBuffer)
        this.NotecardData_Field.ObjectID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.FolderID = unpackUUID(byteBuffer)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
