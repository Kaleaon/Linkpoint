package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetSimPresenceInDatabase : SLMessage {
    SimData SimData_Field = SimData()

    class SimData {
        Int AgentCount
        Int GridX
        Int GridY
        ByteArray HostName
        Int PID
        UUID RegionID
        ByteArray Status
        Int TimeToLive
    }

    SetSimPresenceInDatabase() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.SimData_Field.HostName.size + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.SimData_Field.Status.size + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSetSimPresenceInDatabase(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.ETB)
        packUUID(byteBuffer, this.SimData_Field.RegionID)
        packVariable(byteBuffer, this.SimData_Field.HostName, 1)
        packInt(byteBuffer, this.SimData_Field.GridX)
        packInt(byteBuffer, this.SimData_Field.GridY)
        packInt(byteBuffer, this.SimData_Field.PID)
        packInt(byteBuffer, this.SimData_Field.AgentCount)
        packInt(byteBuffer, this.SimData_Field.TimeToLive)
        packVariable(byteBuffer, this.SimData_Field.Status, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.SimData_Field.RegionID = unpackUUID(byteBuffer)
        this.SimData_Field.HostName = unpackVariable(byteBuffer, 1)
        this.SimData_Field.GridX = unpackInt(byteBuffer)
        this.SimData_Field.GridY = unpackInt(byteBuffer)
        this.SimData_Field.PID = unpackInt(byteBuffer)
        this.SimData_Field.AgentCount = unpackInt(byteBuffer)
        this.SimData_Field.TimeToLive = unpackInt(byteBuffer)
        this.SimData_Field.Status = unpackVariable(byteBuffer, 1)
    }
}
