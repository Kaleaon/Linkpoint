package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class CreateGroupReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var ReplyData_Field: ReplyData = ReplyData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class ReplyData {
        var GroupID: UUID = UUIDPool.ZeroUUID
        var Message: ByteArray = ByteArray(0)
        var Success: Boolean = false
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.ReplyData_Field.Message.size + 17 + 1 + 1 + 16
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCreateGroupReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(32.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packBoolean(byteBuffer, this.ReplyData_Field.Success)
        packVariable(byteBuffer, this.ReplyData_Field.Message, 1)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Success = unpackBoolean(byteBuffer)
        this.ReplyData_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
