package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ReplyTaskInventory : SLMessage {
    InventoryData InventoryData_Field = InventoryData()

    class InventoryData {
        Byte[] Filename
        Int Serial
        UUID TaskID
    }

    ReplyTaskInventory() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.InventoryData_Field.Filename.length + 19 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleReplyTaskInventory(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 34)
        packUUID(byteBuffer, this.InventoryData_Field.TaskID)
        packShort(byteBuffer, (Short) this.InventoryData_Field.Serial)
        packVariable(byteBuffer, this.InventoryData_Field.Filename, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.InventoryData_Field.TaskID = unpackUUID(byteBuffer)
        this.InventoryData_Field.Serial = unpackShort(byteBuffer)
        this.InventoryData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
