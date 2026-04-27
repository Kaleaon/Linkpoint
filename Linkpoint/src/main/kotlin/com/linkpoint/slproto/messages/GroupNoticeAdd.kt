package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupNoticeAdd : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MessageBlock MessageBlock_Field = MessageBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class MessageBlock {
        public ByteArray BinaryBucket
        public Int Dialog
        public ByteArray FromAgentName
        public UUID ID
        public ByteArray Message
        public UUID ToGroupID
    }

    public GroupNoticeAdd() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.MessageBlock_Field.FromAgentName.length + 34 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 20
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupNoticeAdd(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 61)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.MessageBlock_Field.ToGroupID)
        packUUID(byteBuffer, this.MessageBlock_Field.ID)
        packByte(byteBuffer, (Byte) this.MessageBlock_Field.Dialog)
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1)
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2)
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ToGroupID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1)
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2)
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2)
    }
}
