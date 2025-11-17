package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LandStatRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    RequestData RequestData_Field = RequestData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class RequestData {
        byte[] Filter
        Int ParcelLocalID
        Int ReportType
        Int RequestFlags
    }

    LandStatRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.RequestData_Field.Filter.length + 9 + 4 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -91)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.RequestData_Field.ReportType)
        packInt(byteBuffer, this.RequestData_Field.RequestFlags)
        packVariable(byteBuffer, this.RequestData_Field.Filter, 1)
        packInt(byteBuffer, this.RequestData_Field.ParcelLocalID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RequestData_Field.ReportType = unpackInt(byteBuffer)
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer)
        this.RequestData_Field.Filter = unpackVariable(byteBuffer, 1)
        this.RequestData_Field.ParcelLocalID = unpackInt(byteBuffer)
    }
}
