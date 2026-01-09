package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LiveHelpGroupRequest : SLMessage {
    RequestData RequestData_Field = RequestData()

    class RequestData {
        UUID AgentID
        UUID RequestID
    }

    constructor() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler): Unit {
        sLMessageHandler.HandleLiveHelpGroupRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 123)
        packUUID(byteBuffer, this.RequestData_Field.RequestID)
        packUUID(byteBuffer, this.RequestData_Field.AgentID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer): Unit {
        this.RequestData_Field.RequestID = unpackUUID(byteBuffer)
        this.RequestData_Field.AgentID = unpackUUID(byteBuffer)
    }
}
