package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocation : SLMessage {
    StartLocationData StartLocationData_Field = StartLocationData()

    class StartLocationData {
        UUID AgentID
        Int LocationID
        LLVector3 LocationLookAt
        LLVector3 LocationPos
        Long RegionHandle
        UUID RegionID
    }

    SetStartLocation() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSetStartLocation(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 69)
        packUUID(byteBuffer, this.StartLocationData_Field.AgentID)
        packUUID(byteBuffer, this.StartLocationData_Field.RegionID)
        packInt(byteBuffer, this.StartLocationData_Field.LocationID)
        packLong(byteBuffer, this.StartLocationData_Field.RegionHandle)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.StartLocationData_Field.AgentID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.RegionID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer)
        this.StartLocationData_Field.RegionHandle = unpackLong(byteBuffer)
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer)
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer)
    }
}
