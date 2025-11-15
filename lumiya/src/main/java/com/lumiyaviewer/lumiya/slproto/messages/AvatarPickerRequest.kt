package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AvatarPickerRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()

    class AgentData {
        UUID AgentID
        UUID QueryID
        UUID SessionID
    }

    class Data {
        byte[] Name
    }

    AvatarPickerRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.Name.length + 1 + 52
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarPickerRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put(Ascii.SUB)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packVariable(byteBuffer, this.Data_Field.Name, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        this.Data_Field.Name = unpackVariable(byteBuffer, 1)
    }
}
