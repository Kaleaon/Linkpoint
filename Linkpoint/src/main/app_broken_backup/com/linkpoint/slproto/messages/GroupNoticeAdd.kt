package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupNoticeAdd : SLMessage {
    AgentData AgentData_Field = AgentData()
    MessageBlock MessageBlock_Field = MessageBlock()

    class AgentData {
        UUID AgentID
    }

    class MessageBlock {
        ByteArray BinaryBucket
        Int Dialog
        ByteArray FromAgentName
        UUID ID
        ByteArray Message
        UUID ToGroupID
    }

    GroupNoticeAdd() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.MessageBlock_Field.FromAgentName.size + 34 + 2 + this.MessageBlock_Field.Message.size + 2 + this.MessageBlock_Field.BinaryBucket.size + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupNoticeAdd(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 61)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.MessageBlock_Field.ToGroupID)
        packUUID(byteBuffer, this.MessageBlock_Field.ID)
        packByte(byteBuffer, (this as byte).MessageBlock_Field.Dialog)
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1)
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2)
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ToGroupID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1)
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2)
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2)
    }
}
