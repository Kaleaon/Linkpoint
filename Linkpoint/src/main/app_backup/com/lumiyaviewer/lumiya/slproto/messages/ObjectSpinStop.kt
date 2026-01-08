package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ObjectSpinStop : SLMessage {
    AgentData AgentData_Field = AgentData()
    ObjectData ObjectData_Field = ObjectData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        UUID ObjectID
    }

    ObjectSpinStop() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 52
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectSpinStop(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 122)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
    }
}
