package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupReply : SLMessage() {
    public ReplyData ReplyData_Field = ReplyData()

    @JvmStatic
    class ReplyData {
        public UUID GroupID
        public UUID RequestID
        public Byte[] Selection
    }

    public LiveHelpGroupReply() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ReplyData_Field.Selection.length + 33 + 4
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 124)
        packUUID(byteBuffer, this.ReplyData_Field.RequestID)
        packUUID(byteBuffer, this.ReplyData_Field.GroupID)
        packVariable(byteBuffer, this.ReplyData_Field.Selection, 1)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ReplyData_Field.RequestID = unpackUUID(byteBuffer)
        this.ReplyData_Field.GroupID = unpackUUID(byteBuffer)
        this.ReplyData_Field.Selection = unpackVariable(byteBuffer, 1)
    }
}
