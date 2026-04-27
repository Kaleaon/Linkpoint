package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class AgentMovementComplete : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public Data Data_Field = Data()
    public SimData SimData_Field = SimData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Data {
        public LLVector3 LookAt
        public LLVector3 Position
        public Long RegionHandle
        public Int Timestamp
    }

    @JvmStatic
    class SimData {
        public ByteArray ChannelVersion
    }

    public AgentMovementComplete() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.SimData_Field.ChannelVersion.length + 2 + 72
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAgentMovementComplete(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -6)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLVector3(byteBuffer, this.Data_Field.Position)
        packLLVector3(byteBuffer, this.Data_Field.LookAt)
        packLong(byteBuffer, this.Data_Field.RegionHandle)
        packInt(byteBuffer, this.Data_Field.Timestamp)
        packVariable(byteBuffer, this.SimData_Field.ChannelVersion, 2)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.Data_Field.Position = unpackLLVector3(byteBuffer)
        this.Data_Field.LookAt = unpackLLVector3(byteBuffer)
        this.Data_Field.RegionHandle = unpackLong(byteBuffer)
        this.Data_Field.Timestamp = unpackInt(byteBuffer)
        this.SimData_Field.ChannelVersion = unpackVariable(byteBuffer, 2)
    }
}
