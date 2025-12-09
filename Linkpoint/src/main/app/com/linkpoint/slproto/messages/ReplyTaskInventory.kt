package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ReplyTaskInventory : SLMessage {
    InventoryData InventoryData_Field = InventoryData()

    class InventoryData {
        ByteArray Filename
        Int Serial
        UUID TaskID
    }

    ReplyTaskInventory() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.InventoryData_Field.Filename.size + 19 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleReplyTaskInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 34)
        packUUID(byteBuffer, this.InventoryData_Field.TaskID)
        packShort(byteBuffer, (this as Short).InventoryData_Field.Serial)
        packVariable(byteBuffer, this.InventoryData_Field.Filename, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.InventoryData_Field.TaskID = unpackUUID(byteBuffer)
        this.InventoryData_Field.Serial = unpackShort(byteBuffer)
        this.InventoryData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
