package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return this.SimData_Field.HostName.length + 17 + 4 + 4 + 4 + 4 + 4 + 1 + this.SimData_Field.Status.length + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetSimPresenceInDatabase(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
