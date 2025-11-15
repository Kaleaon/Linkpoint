package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelGodForceOwner : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        Int LocalID
        UUID OwnerID
    }

    ParcelGodForceOwner() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 56
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelGodForceOwner(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -42)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.Data_Field.OwnerID)
        packInt(byteBuffer, this.Data_Field.LocalID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.OwnerID = unpackUUID(byteBuffer)
        this.Data_Field.LocalID = unpackInt(byteBuffer)
    }
}
