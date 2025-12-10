package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class SetStartLocationRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    StartLocationData StartLocationData_Field = StartLocationData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class StartLocationData {
        Int LocationID
        LLVector3 LocationLookAt
        LLVector3 LocationPos
        ByteArray SimName
    }

    SetStartLocationRequest() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.StartLocationData_Field.SimName.size + 1 + 4 + 12 + 12 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleSetStartLocationRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 68)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packVariable(byteBuffer, this.StartLocationData_Field.SimName, 1)
        packInt(byteBuffer, this.StartLocationData_Field.LocationID)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos)
        packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.StartLocationData_Field.SimName = unpackVariable(byteBuffer, 1)
        this.StartLocationData_Field.LocationID = unpackInt(byteBuffer)
        this.StartLocationData_Field.LocationPos = unpackLLVector3(byteBuffer)
        this.StartLocationData_Field.LocationLookAt = unpackLLVector3(byteBuffer)
    }
}
