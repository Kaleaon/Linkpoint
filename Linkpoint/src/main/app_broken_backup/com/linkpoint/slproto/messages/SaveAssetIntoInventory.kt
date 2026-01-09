package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SaveAssetIntoInventory : SLMessage {
    AgentData AgentData_Field = AgentData()
    InventoryData InventoryData_Field = InventoryData()

    class AgentData {
        UUID AgentID
    }

    class InventoryData {
        UUID ItemID
        UUID NewAssetID
    }

    SaveAssetIntoInventory() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSaveAssetIntoInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 16)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.InventoryData_Field.ItemID)
        packUUID(byteBuffer, this.InventoryData_Field.NewAssetID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer)
        this.InventoryData_Field.NewAssetID = unpackUUID(byteBuffer)
    }
}
