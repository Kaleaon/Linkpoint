package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelObjectOwnersRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    ParcelData ParcelData_Field = ParcelData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ParcelData {
        Int LocalID
    }

    ParcelObjectOwnersRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 40
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelObjectOwnersRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 56)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.ParcelData_Field.LocalID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer)
    }
}
