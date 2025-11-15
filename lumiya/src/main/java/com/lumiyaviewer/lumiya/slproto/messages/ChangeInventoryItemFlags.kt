package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ChangeInventoryItemFlags : SLMessage {
    AgentData AgentData_Field
    ArrayList<InventoryData> InventoryData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class InventoryData {
        Int Flags
        UUID ItemID
    }

    ChangeInventoryItemFlags() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        return (this.InventoryData_Fields.size() * 20) + 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChangeInventoryItemFlags(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 15)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((byte) this.InventoryData_Fields.size())
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID)
            packInt(byteBuffer, inventoryData.Flags)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            InventoryData inventoryData = InventoryData()
            inventoryData.ItemID = unpackUUID(byteBuffer)
            inventoryData.Flags = unpackInt(byteBuffer)
            this.InventoryData_Fields.add(inventoryData)
        }
    }
}
