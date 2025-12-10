package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class CopyInventoryItem : SLMessage {
    AgentData AgentData_Field
    ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryData {
        Int CallbackID
        UUID NewFolderID
        ByteArray NewName
        UUID OldAgentID
        UUID OldItemID
    }

    CopyInventoryItem() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 37
        Iterator<T> it = this.InventoryData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as InventoryData).next()).NewName.size + 53 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleCopyInventoryItem(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put(Ascii.CR)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as byte).InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packInt(byteBuffer, inventoryData.CallbackID)
            packUUID(byteBuffer, inventoryData.OldAgentID)
            packUUID(byteBuffer, inventoryData.OldItemID)
            packUUID(byteBuffer, inventoryData.NewFolderID)
            packVariable(byteBuffer, inventoryData.NewName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            InventoryData inventoryData = InventoryData()
            inventoryData.CallbackID = unpackInt(byteBuffer)
            inventoryData.OldAgentID = unpackUUID(byteBuffer)
            inventoryData.OldItemID = unpackUUID(byteBuffer)
            inventoryData.NewFolderID = unpackUUID(byteBuffer)
            inventoryData.NewName = unpackVariable(byteBuffer, 1)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
