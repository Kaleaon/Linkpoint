package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventNotificationRemoveRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public EventData EventData_Field = EventData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class EventData {
        public Int EventID
    }

    public EventNotificationRemoveRequest() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEventNotificationRemoveRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -74)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
