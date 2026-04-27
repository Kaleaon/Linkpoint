package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CreateGroupReply : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ReplyData ReplyData_Field = ReplyData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class ReplyData {
        public UUID GroupID
        public ByteArray Message
        public Boolean Success
    }

    public CreateGroupReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ReplyData_Field.Message.length + 18 + 20
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCreateGroupReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 84)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packBoolean(byteBuffer, this.ReplyData_Field.Success)
        packVariable(byteBuffer, this.ReplyData_Field.Message, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Success = unpackBoolean(byteBuffer)
        this.ReplyData_Field.Message = unpackVariable(byteBuffer, 1)
    }
}
