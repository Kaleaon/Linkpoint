package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChatFromSimulator : SLMessage {
    ChatData ChatData_Field = ChatData()

    class ChatData {
        Int Audible
        Int ChatType
        ByteArray FromName
        ByteArray Message
        UUID OwnerID
        LLVector3 Position
        UUID SourceID
        Int SourceType
    }

    ChatFromSimulator() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ChatData_Field.FromName.size + 1 + 16 + 16 + 1 + 1 + 1 + 12 + 2 + this.ChatData_Field.Message.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleChatFromSimulator(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -117)
        packVariable(byteBuffer, this.ChatData_Field.FromName, 1)
        packUUID(byteBuffer, this.ChatData_Field.SourceID)
        packUUID(byteBuffer, this.ChatData_Field.OwnerID)
        packByte(byteBuffer, (this as byte).ChatData_Field.SourceType)
        packByte(byteBuffer, (this as byte).ChatData_Field.ChatType)
        packByte(byteBuffer, (this as byte).ChatData_Field.Audible)
        packLLVector3(byteBuffer, this.ChatData_Field.Position)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.ChatData_Field.FromName = unpackVariable(byteBuffer, 1)
        this.ChatData_Field.SourceID = unpackUUID(byteBuffer)
        this.ChatData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ChatData_Field.SourceType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.ChatType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Audible = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Position = unpackLLVector3(byteBuffer)
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2)
    }
}
