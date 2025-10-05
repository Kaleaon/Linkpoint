package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TransferInventoryAck : SLMessage() {
    val InfoBlock_Field = InfoBlock()

    class InfoBlock {
        var TransactionID: UUID? = null
        var InventoryID: UUID? = null
    }

    init {
        zeroCoded = true
    }

    override fun CalcPayloadSize(): Int = 36

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTransferInventoryAck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(40.toByte())
        packUUID(buffer, InfoBlock_Field.TransactionID)
        packUUID(buffer, InfoBlock_Field.InventoryID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        InfoBlock_Field.TransactionID = unpackUUID(buffer)
        InfoBlock_Field.InventoryID = unpackUUID(buffer)
    }
}