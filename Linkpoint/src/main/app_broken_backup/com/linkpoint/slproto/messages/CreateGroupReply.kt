package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateGroupReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    ReplyData ReplyData_Field = ReplyData()

    class AgentData {
        UUID AgentID
    }

    class ReplyData {
        UUID GroupID
        ByteArray Message
        Boolean Success
    }

    CreateGroupReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ReplyData_Field.Message.size + 18 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCreateGroupReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packBoolean(byteBuffer, this.ReplyData_Field.Success)
        packVariable(byteBuffer, this.ReplyData_Field.Message, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Success = unpackBoolean(byteBuffer)
        this.ReplyData_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
