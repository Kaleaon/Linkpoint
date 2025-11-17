package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveAttachment : SLMessage {
    AgentData AgentData_Field = AgentData()
    AttachmentBlock AttachmentBlock_Field = AttachmentBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class AttachmentBlock {
        Int AttachmentPoint
        UUID ItemID
    }

    RemoveAttachment() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 53
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveAttachment(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 76)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.AttachmentBlock_Field.AttachmentPoint)
        packUUID(byteBuffer, this.AttachmentBlock_Field.ItemID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AttachmentBlock_Field.ItemID = unpackUUID(byteBuffer)
    }
}
