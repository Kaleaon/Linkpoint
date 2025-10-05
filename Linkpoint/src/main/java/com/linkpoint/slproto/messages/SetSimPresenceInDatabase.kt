package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetSimPresenceInDatabase : SLMessage() {
    val SimData_Field = SimData()

    class SimData {
        var RegionID: UUID? = null
        lateinit var HostName: ByteArray
        var GridX: Int = 0
        var GridY: Int = 0
        var PID: Int = 0
        var AgentCount: Int = 0
        var TimeToLive: Int = 0
        lateinit var Status: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return SimData_Field.HostName.size + 17 + 4 + 4 + 4 + 4 + 4 + 1 + SimData_Field.Status.size + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSetSimPresenceInDatabase(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(Ascii.ETB)
        packUUID(buffer, SimData_Field.RegionID)
        packVariable(buffer, SimData_Field.HostName, 1)
        packInt(buffer, SimData_Field.GridX)
        packInt(buffer, SimData_Field.GridY)
        packInt(buffer, SimData_Field.PID)
        packInt(buffer, SimData_Field.AgentCount)
        packInt(buffer, SimData_Field.TimeToLive)
        packVariable(buffer, SimData_Field.Status, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        SimData_Field.RegionID = unpackUUID(buffer)
        SimData_Field.HostName = unpackVariable(buffer, 1)
        SimData_Field.GridX = unpackInt(buffer)
        SimData_Field.GridY = unpackInt(buffer)
        SimData_Field.PID = unpackInt(buffer)
        SimData_Field.AgentCount = unpackInt(buffer)
        SimData_Field.TimeToLive = unpackInt(buffer)
        SimData_Field.Status = unpackVariable(buffer, 1)
    }
}