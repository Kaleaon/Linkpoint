package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ChatFromViewer : SLMessage {
    AgentData AgentData_Field = AgentData()
    ChatData ChatData_Field = ChatData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ChatData {
        Int Channel
        byte[] Message
        Int Type
    }

    ChatFromViewer() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.ChatData_Field.Message.length + 2 + 1 + 4 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChatFromViewer(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 80)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.ChatData_Field.Message, 2)
        packByte(byteBuffer, (byte) this.ChatData_Field.Type)
        packInt(byteBuffer, this.ChatData_Field.Channel)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ChatData_Field.Message = unpackVariable(byteBuffer, 2)
        this.ChatData_Field.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ChatData_Field.Channel = unpackInt(byteBuffer)
    }
}
