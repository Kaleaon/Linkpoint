package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ReplyTaskInventory : SLMessage() {
    public InventoryData InventoryData_Field = InventoryData()

    @JvmStatic
    class InventoryData {
        public ByteArray Filename
        public Int Serial
        public UUID TaskID
    }

    public ReplyTaskInventory() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.InventoryData_Field.Filename.length + 19 + 4
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleReplyTaskInventory(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 34)
        packUUID(byteBuffer, this.InventoryData_Field.TaskID)
        packShort(byteBuffer, (Short) this.InventoryData_Field.Serial)
        packVariable(byteBuffer, this.InventoryData_Field.Filename, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.InventoryData_Field.TaskID = unpackUUID(byteBuffer)
        this.InventoryData_Field.Serial = unpackShort(byteBuffer)
        this.InventoryData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
