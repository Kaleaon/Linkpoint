package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class MoveInventoryItem : SLMessage {
    AgentData AgentData_Field
    ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
        Boolean Stamp
    }

    class InventoryData {
        UUID FolderID
        UUID ItemID
        Byte[] NewName
    }

    MoveInventoryItem() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int i = 38
        Iterator<T> it = this.InventoryData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((InventoryData) it.next()).NewName.length + 33 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMoveInventoryItem(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.Stamp = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.FolderID = unpackUUID(byteBuffer)
            inventoryData.NewName = unpackVariable(byteBuffer, 1)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
