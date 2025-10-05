package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelDwellRequest : SLMessage() {
    val AgentData_Field = AgentData()
    val Data_Field = Data()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class Data {
        var LocalID: Int = 0
        var ParcelID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 56

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleParcelDwellRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-38).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, Data_Field.LocalID)
        packUUID(buffer, Data_Field.ParcelID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        Data_Field.LocalID = unpackInt(buffer)
        Data_Field.ParcelID = unpackUUID(buffer)
    }
}