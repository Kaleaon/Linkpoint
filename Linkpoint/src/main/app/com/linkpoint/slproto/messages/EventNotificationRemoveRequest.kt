package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventNotificationRemoveRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    EventData EventData_Field = EventData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class EventData {
        Int EventID
    }

    EventNotificationRemoveRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEventNotificationRemoveRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -74)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.EventData_Field.EventID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.EventData_Field.EventID = unpackInt(byteBuffer)
    }
}
