package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupRequest : SLMessage() {
    public RequestData RequestData_Field = RequestData()

    @JvmStatic
    class RequestData {
        public UUID AgentID
        public UUID RequestID
    }

    public LiveHelpGroupRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 36
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLiveHelpGroupRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 123)
        packUUID(byteBuffer, this.RequestData_Field.RequestID)
        packUUID(byteBuffer, this.RequestData_Field.AgentID)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestData_Field.RequestID = unpackUUID(byteBuffer)
        this.RequestData_Field.AgentID = unpackUUID(byteBuffer)
    }
}
