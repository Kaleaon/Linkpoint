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

    Int CalcPayloadSize() {
        return 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDetachAttachmentIntoInv(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -115)
        packUUID(byteBuffer, this.ObjectData_Field.AgentID)
        packUUID(byteBuffer, this.ObjectData_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.AgentID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ItemID = unpackUUID(byteBuffer)
    }
}
