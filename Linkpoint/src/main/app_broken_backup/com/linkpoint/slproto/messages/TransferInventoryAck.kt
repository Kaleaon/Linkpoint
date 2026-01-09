package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferInventoryAck : SLMessage {
    InfoBlock InfoBlock_Field = InfoBlock()

    class InfoBlock {
        UUID InventoryID
        UUID TransactionID
    }

    TransferInventoryAck() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTransferInventoryAck(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 40)
        packUUID(byteBuffer, this.InfoBlock_Field.TransactionID)
        packUUID(byteBuffer, this.InfoBlock_Field.InventoryID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.InfoBlock_Field.TransactionID = unpackUUID(byteBuffer)
        this.InfoBlock_Field.InventoryID = unpackUUID(byteBuffer)
    }
}
