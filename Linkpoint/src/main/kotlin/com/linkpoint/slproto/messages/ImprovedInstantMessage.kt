package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ImprovedInstantMessage : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public MessageBlock MessageBlock_Field = MessageBlock()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class MessageBlock {
        public ByteArray BinaryBucket
        public Int Dialog
        public ByteArray FromAgentName
        public Boolean FromGroup
        public UUID ID
        public ByteArray Message
        public Int Offline
        public Int ParentEstateID
        public LLVector3 Position
        public UUID RegionID
        public Int Timestamp
        public UUID ToAgentID
    }

    public ImprovedInstantMessage() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.MessageBlock_Field.FromAgentName.length + 72 + 2 + this.MessageBlock_Field.Message.length + 2 + this.MessageBlock_Field.BinaryBucket.length + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleImprovedInstantMessage(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -2)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packBoolean(byteBuffer, this.MessageBlock_Field.FromGroup)
        packUUID(byteBuffer, this.MessageBlock_Field.ToAgentID)
        packInt(byteBuffer, this.MessageBlock_Field.ParentEstateID)
        packUUID(byteBuffer, this.MessageBlock_Field.RegionID)
        packLLVector3(byteBuffer, this.MessageBlock_Field.Position)
        packByte(byteBuffer, (Byte) this.MessageBlock_Field.Offline)
        packByte(byteBuffer, (Byte) this.MessageBlock_Field.Dialog)
        packUUID(byteBuffer, this.MessageBlock_Field.ID)
        packInt(byteBuffer, this.MessageBlock_Field.Timestamp)
        packVariable(byteBuffer, this.MessageBlock_Field.FromAgentName, 1)
        packVariable(byteBuffer, this.MessageBlock_Field.Message, 2)
        packVariable(byteBuffer, this.MessageBlock_Field.BinaryBucket, 2)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
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
