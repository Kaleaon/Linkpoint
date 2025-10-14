package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupNoticeAdd : SLMessage {
    AgentData AgentData_Field = AgentData()
    MessageBlock MessageBlock_Field = MessageBlock()

    class AgentData {
        UUID AgentID
    }

    class MessageBlock {
        byte[] BinaryBucket
        Int Dialog
        byte[] FromAgentName
        UUID ID
        byte[] Message
        UUID ToGroupID
    }

    GroupNoticeAdd() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.MessageBlock_Field.FromAgentName.length + 34 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupNoticeAdd(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 61)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.MessageBlock_Field.ToGroupID)
        packUUID(byteBuffer, this.MessageBlock_Field.ID)
        packByte(byteBuffer, (byte) this.MessageBlock_Field.Dialog)
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1)
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2)
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ToGroupID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1)
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2)
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2)
    }
}
