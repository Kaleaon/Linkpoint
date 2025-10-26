package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ChatFromViewer : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ChatData ChatData_Field = ChatData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ChatData {
        public Int Channel
        public ByteArray Message
        public Int Type
    }

    public ChatFromViewer() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.ChatData_Field.Message.length + 2 + 1 + 4 + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleChatFromViewer(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 80)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
        packByte(byteBuffer, (Byte) this.ChatData_Field.Type)
        packInt(byteBuffer, this.ChatData_Field.Channel)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2)
        this.ChatData_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Channel = unpackInt(byteBuffer)
    }
}
