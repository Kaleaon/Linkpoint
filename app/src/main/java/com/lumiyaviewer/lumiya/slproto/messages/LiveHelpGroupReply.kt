package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupReply : SLMessage {
    ReplyData ReplyData_Field = ReplyData()

    class ReplyData {
        UUID GroupID
        UUID RequestID
        Byte[] Selection
    }

    LiveHelpGroupReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ReplyData_Field.Selection.length + 33 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 124)
        packUUID(byteBuffer, this.ReplyData_Field.RequestID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packVariable(byteBuffer, this.ReplyData_Field.Selection, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyData_Field.RequestID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Selection = unpackVariable(byteBuffer, 1)
    }
}
