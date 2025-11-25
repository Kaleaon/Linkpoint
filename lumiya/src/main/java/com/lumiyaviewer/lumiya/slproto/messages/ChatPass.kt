package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class ChatPass : SLMessage {
    var ChatData_Field: ChatData = ChatData()

    class ChatData {
        var Channel: Int = 0
        var ID: UUID = UUIDPool.ZeroUUID
        var Message: ByteArray = ByteArray(0)
        var Name: ByteArray = ByteArray(0)
        var OwnerID: UUID = UUIDPool.ZeroUUID
        var Position: LLVector3 = LLVector3()
        var Radius: Float = 0f
        var SimAccess: Int = 0
        var SourceType: Int = 0
        var Type: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.ChatData_Field.Name.size + 49 + 1 + 1 + 4 + 1 + 2 + this.ChatData_Field.Message.size + 4
    }

    // Handle needs to be implemented or stubbed, checking SLMessageHandler for ChatPass support
    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // Assuming SLMessageHandler doesn't have HandleChatPass yet, or I need to add it
        // sLMessageHandler.HandleChatPass(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-17).toByte())
        packInt(byteBuffer, this.ChatData_Field.Channel)
        packLLVector3(byteBuffer, this.ChatData_Field.Position)
        packUUID(byteBuffer, this.ChatData_Field.ID)
        packUUID(byteBuffer, this.ChatData_Field.OwnerID)
        packVariable(byteBuffer, this.ChatData_Field.Name, 1)
        packByte(byteBuffer, this.ChatData_Field.SourceType.toByte())
        packByte(byteBuffer, this.ChatData_Field.Type.toByte())
        packFloat(byteBuffer, this.ChatData_Field.Radius)
        packByte(byteBuffer, this.ChatData_Field.SimAccess.toByte())
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.ChatData_Field.Channel = unpackInt(byteBuffer)
        this.ChatData_Field.Position = unpackLLVector3(byteBuffer)
        this.ChatData_Field.ID = unpackUUID(byteBuffer)
        this.ChatData_Field.OwnerID = unpackUUID(byteBuffer)
        this.ChatData_Field.Name = unpackVariable(byteBuffer, 1)
        this.ChatData_Field.SourceType = unpackByte(byteBuffer).toInt() and 0xFF
        this.ChatData_Field.Type = unpackByte(byteBuffer).toInt() and 0xFF
        this.ChatData_Field.Radius = unpackFloat(byteBuffer)
        this.ChatData_Field.SimAccess = unpackByte(byteBuffer).toInt() and 0xFF
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2)
    }
}
