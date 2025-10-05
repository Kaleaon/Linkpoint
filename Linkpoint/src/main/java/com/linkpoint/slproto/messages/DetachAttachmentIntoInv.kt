package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DetachAttachmentIntoInv : SLMessage() {
    val ObjectData_Field = ObjectData()

    class ObjectData {
        var AgentID: UUID? = null
        var ItemID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 36

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleDetachAttachmentIntoInv(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-115).toByte())
        packUUID(buffer, ObjectData_Field.AgentID)
        packUUID(buffer, ObjectData_Field.ItemID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        ObjectData_Field.AgentID = unpackUUID(buffer)
        ObjectData_Field.ItemID = unpackUUID(buffer)
    }
}