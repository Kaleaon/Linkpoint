package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ReplyTaskInventory : SLMessage() {
    public InventoryData InventoryData_Field = InventoryData()

    @JvmStatic
    class InventoryData {
        public Byte[] Filename
        public Int Serial
        public UUID TaskID
    }

    public ReplyTaskInventory() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.InventoryData_Field.Filename.length + 19 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleReplyTaskInventory(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 34)
        packUUID(byteBuffer, this.InventoryData_Field.TaskID)
        packShort(byteBuffer, (Short) this.InventoryData_Field.Serial)
        packVariable(byteBuffer, this.InventoryData_Field.Filename, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.InventoryData_Field.TaskID = unpackUUID(byteBuffer)
        this.InventoryData_Field.Serial = unpackShort(byteBuffer)
        this.InventoryData_Field.Filename = unpackVariable(byteBuffer, 1)
    }
}
