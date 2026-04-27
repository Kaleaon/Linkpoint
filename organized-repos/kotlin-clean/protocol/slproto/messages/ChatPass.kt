package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChatPass : SLMessage() {
    public ChatData ChatData_Field = ChatData()

    @JvmStatic
    class ChatData {
        public Int Channel
        public UUID ID
        public ByteArray Message
        public ByteArray Name
        public UUID OwnerID
        public LLVector3 Position
        public Float Radius
        public Int SimAccess
        public Int SourceType
        public Int Type
    }

    public ChatPass() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return this.ChatData_Field.Name.length + 49 + 1 + 1 + 4 + 1 + 2 + this.ChatData_Field.Message.length + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatPass(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -17)
        packInt(byteBuffer, this.ChatData_Field.Channel)
        packLLVector3(byteBuffer, this.ChatData_Field.Position)
        packUUID(byteBuffer, this.ChatData_Field.ID)
        packUUID(byteBuffer, this.ChatData_Field.OwnerID)
        packVariable(byteBuffer, this.ChatData_Field.Name, 1)
        packByte(byteBuffer, (Byte) this.ChatData_Field.SourceType)
        packByte(byteBuffer, (Byte) this.ChatData_Field.Type)
        packFloat(byteBuffer, this.ChatData_Field.Radius)
        packByte(byteBuffer, (Byte) this.ChatData_Field.SimAccess)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.ChatData_Field.Channel = unpackInt(byteBuffer)
        this.ChatData_Field.Position = unpackLLVector3(byteBuffer)
        this.ChatData_Field.ID = unpackUUID(byteBuffer)
        this.ChatData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ChatData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ChatData_Field.SourceType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Radius = unpackFloat(byteBuffer)
        this.ChatData_Field.SimAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2)
    }
}
