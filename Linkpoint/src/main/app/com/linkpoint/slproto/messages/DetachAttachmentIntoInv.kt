package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DetachAttachmentIntoInv : SLMessage {
    ObjectData ObjectData_Field = ObjectData()

    class ObjectData {
        UUID AgentID
        UUID ItemID
    }

    DetachAttachmentIntoInv() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDetachAttachmentIntoInv(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -115)
        packUUID(byteBuffer, this.ObjectData_Field.AgentID)
        packUUID(byteBuffer, this.ObjectData_Field.ItemID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ObjectData_Field.AgentID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
