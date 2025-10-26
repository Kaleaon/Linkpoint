package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class CopyInventoryItem : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class InventoryData {
        public Int CallbackID
        public UUID NewFolderID
        public ByteArray NewName
        public UUID OldAgentID
        public UUID OldItemID
    }

    public CopyInventoryItem() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 37
        val it: Iterator<T> = this.InventoryData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((InventoryData) it.next()).NewName.length + 53 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleCopyInventoryItem(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.CR)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packInt(byteBuffer, inventoryData.CallbackID)
            packUUID(byteBuffer, inventoryData.OldAgentID)
            packUUID(byteBuffer, inventoryData.OldItemID)
            packUUID(byteBuffer, inventoryData.NewFolderID)
            packVariable(byteBuffer, inventoryData.NewName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val inventoryData: InventoryData = InventoryData()
            inventoryData.CallbackID = unpackInt(byteBuffer)
            inventoryData.OldAgentID = unpackUUID(byteBuffer)
            inventoryData.OldItemID = unpackUUID(byteBuffer)
            inventoryData.NewFolderID = unpackUUID(byteBuffer)
            inventoryData.NewName = unpackVariable(byteBuffer, 1)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
