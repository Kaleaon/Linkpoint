package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MoveInventoryItem : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
        public Boolean Stamp
    }

    @JvmStatic
    class InventoryData {
        public UUID FolderID
        public UUID ItemID
        public ByteArray NewName
    }

    public MoveInventoryItem() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 38
        val it: Iterator<T> = this.InventoryData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((InventoryData) it.next()).NewName.length + 33 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleMoveInventoryItem(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put(Ascii.FF)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.AgentData_Field.Stamp)
        byteBuffer.put((Byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
            packUUID(byteBuffer, inventoryData.FolderID)
            packVariable(byteBuffer, inventoryData.NewName, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Stamp = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val inventoryData: InventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.FolderID = unpackUUID(byteBuffer)
            inventoryData.NewName = unpackVariable(byteBuffer, 1)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
