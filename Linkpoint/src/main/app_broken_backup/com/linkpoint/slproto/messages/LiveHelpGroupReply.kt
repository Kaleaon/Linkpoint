package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupReply : SLMessage {
    ReplyData ReplyData_Field = ReplyData()

    class ReplyData {
        UUID GroupID
        UUID RequestID
        ByteArray Selection
    }

    LiveHelpGroupReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ReplyData_Field.Selection.size + 33 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleLiveHelpGroupReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 124)
        packUUID(byteBuffer, this.ReplyData_Field.RequestID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packVariable(byteBuffer, this.ReplyData_Field.Selection, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ReplyData_Field.RequestID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Selection = unpackVariable(byteBuffer, 1)
    }
}
