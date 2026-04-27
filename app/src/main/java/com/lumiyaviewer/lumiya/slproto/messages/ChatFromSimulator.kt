package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ChatFromSimulator : SLMessage {
    ChatData ChatData_Field = ChatData()

    class ChatData {
        Int Audible
        Int ChatType
        byte[] FromName
        byte[] Message
        UUID OwnerID
        LLVector3 Position
        UUID SourceID
        Int SourceType
    }

    ChatFromSimulator() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ChatData_Field.FromName.length + 1 + 16 + 16 + 1 + 1 + 1 + 12 + 2 + this.ChatData_Field.Message.length + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatFromSimulator(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -117)
        packVariable(byteBuffer, this.ChatData_Field.FromName, 1)
        packUUID(byteBuffer, this.ChatData_Field.SourceID)
        packUUID(byteBuffer, this.ChatData_Field.OwnerID)
        packByte(byteBuffer, (byte) this.ChatData_Field.SourceType)
        packByte(byteBuffer, (byte) this.ChatData_Field.ChatType)
        packByte(byteBuffer, (byte) this.ChatData_Field.Audible)
        packLLVector3(byteBuffer, this.ChatData_Field.Position)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
