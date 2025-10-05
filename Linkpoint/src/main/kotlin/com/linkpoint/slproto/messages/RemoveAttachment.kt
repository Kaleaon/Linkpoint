package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class RemoveAttachment : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public AttachmentBlock AttachmentBlock_Field = AttachmentBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class AttachmentBlock {
        public Int AttachmentPoint
        public UUID ItemID
    }

    public RemoveAttachment() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 53
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRemoveAttachment(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 76)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packByte(byteBuffer, (Byte) this.AttachmentBlock_Field.AttachmentPoint)
        packUUID(byteBuffer, this.AttachmentBlock_Field.ItemID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AttachmentBlock_Field.ItemID = unpackUUID(byteBuffer)
    }
}
