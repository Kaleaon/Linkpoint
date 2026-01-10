package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ImprovedInstantMessage : SLMessage {
    AgentData AgentData_Field = AgentData()
    MessageBlock MessageBlock_Field = MessageBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class MessageBlock {
        ByteArray BinaryBucket
        Int Dialog
        ByteArray FromAgentName
        Boolean FromGroup
        UUID ID
        ByteArray Message
        Int Offline
        Int ParentEstateID
        LLVector3 Position
        UUID RegionID
        Int Timestamp
        UUID ToAgentID
    }

    ImprovedInstantMessage() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.MessageBlock_Field.FromAgentName.size + 72 + 2 + this.MessageBlock_Field.Message.size + 2 + this.MessageBlock_Field.BinaryBucket.size + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleImprovedInstantMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.MessageBlock_Field.FromGroup)
        packUUID(byteBuffer, this.MessageBlock_Field.ToAgentID)
        packInt(byteBuffer, this.MessageBlock_Field.ParentEstateID)
        packUUID(byteBuffer, this.MessageBlock_Field.RegionID)
        packLLVector3(byteBuffer, this.MessageBlock_Field.Position)
        packByte(byteBuffer, (this as byte).MessageBlock_Field.Offline)
        packByte(byteBuffer, (this as byte).MessageBlock_Field.Dialog)
        packUUID(byteBuffer, this.MessageBlock_Field.ID)
        packInt(byteBuffer, this.MessageBlock_Field.Timestamp)
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1)
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2)
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.FromGroup = unpackBoolean(byteBuffer)
        this.MessageBlock_Field.ToAgentID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.ParentEstateID = unpackInt(byteBuffer)
        this.MessageBlock_Field.RegionID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.Position = unpackLLVector3(byteBuffer)
        this.MessageBlock_Field.Offline = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MessageBlock_Field.Dialog = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.MessageBlock_Field.ID = unpackUUID(byteBuffer)
        this.MessageBlock_Field.Timestamp = unpackInt(byteBuffer)
        this.MessageBlock_Field.FromAgentName = unpackVariable(byteBuffer, 1)
        this.MessageBlock_Field.Message = unpackVariable(byteBuffer, 2)
        this.MessageBlock_Field.BinaryBucket = unpackVariable(byteBuffer, 2)
    }
}
