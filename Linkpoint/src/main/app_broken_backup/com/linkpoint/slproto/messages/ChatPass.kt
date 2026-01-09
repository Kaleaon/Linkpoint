package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChatPass : SLMessage {
    ChatData ChatData_Field = ChatData()

    class ChatData {
        Int Channel
        UUID ID
        ByteArray Message
        ByteArray Name
        UUID OwnerID
        LLVector3 Position
        float Radius
        Int SimAccess
        Int SourceType
        Int Type
    }

    ChatPass() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.ChatData_Field.Name.size + 49 + 1 + 1 + 4 + 1 + 2 + this.ChatData_Field.Message.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleChatPass(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -17)
        packInt(byteBuffer, this.ChatData_Field.Channel)
        packLLVector3(byteBuffer, this.ChatData_Field.Position)
        packUUID(byteBuffer, this.ChatData_Field.ID)
        packUUID(byteBuffer, this.ChatData_Field.OwnerID)
        packVariable(byteBuffer, this.ChatData_Field.Name, 1)
        packByte(byteBuffer, (this as byte).ChatData_Field.SourceType)
        packByte(byteBuffer, (this as byte).ChatData_Field.Type)
        packFloat(byteBuffer, this.ChatData_Field.Radius)
        packByte(byteBuffer, (this as byte).ChatData_Field.SimAccess)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
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
