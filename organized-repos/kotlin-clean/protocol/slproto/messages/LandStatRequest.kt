package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LandStatRequest : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public RequestData RequestData_Field = RequestData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class RequestData {
        public ByteArray Filter
        public Int ParcelLocalID
        public Int ReportType
        public Int RequestFlags
    }

    public LandStatRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.RequestData_Field.Filter.length + 9 + 4 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLandStatRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -91)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.RequestData_Field.ReportType)
        packInt(byteBuffer, this.RequestData_Field.RequestFlags)
        packVariable(byteBuffer, this.RequestData_Field.Filter, 1)
        packInt(byteBuffer, this.RequestData_Field.ParcelLocalID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.RequestData_Field.ReportType = unpackInt(byteBuffer)
        this.RequestData_Field.RequestFlags = unpackInt(byteBuffer)
        this.RequestData_Field.Filter = unpackVariable(byteBuffer, 1)
        this.RequestData_Field.ParcelLocalID = unpackInt(byteBuffer)
    }
}
