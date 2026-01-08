package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentMovementComplete : SLMessage {
    AgentData AgentData_Field = AgentData()
    Data Data_Field = Data()
    SimData SimData_Field = SimData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Data {
        LLVector3 LookAt
        LLVector3 Position
        Long RegionHandle
        Int Timestamp
    }

    class SimData {
        ByteArray ChannelVersion
    }

    AgentMovementComplete() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.SimData_Field.ChannelVersion.size + 2 + 72
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAgentMovementComplete(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLVector3(byteBuffer, this.Data_Field.Position)
        packLLVector3(byteBuffer, this.Data_Field.LookAt)
        packLong(byteBuffer, this.Data_Field.RegionHandle)
        packInt(byteBuffer, this.Data_Field.Timestamp)
        packVariable(byteBuffer, this.SimData_Field.ChannelVersion, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.Position = unpackLLVector3(byteBuffer)
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer)
        this.Data_Field.RegionHandle = unpackLong(byteBuffer)
        this.Data_Field.Timestamp = unpackInt(byteBuffer)
        this.SimData_Field.ChannelVersion = unpackVariable(byteBuffer, 2)
    }
}
