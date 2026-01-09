package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CoarseLocationUpdate : SLMessage {
    ArrayList<AgentData> AgentData_Fields = ArrayList<>()
    Index Index_Field
    ArrayList<Location> Location_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class Index {
        Int Prey
        Int You
    }

    class Location {
        Int X
        Int Y
        Int Z
    }

    CoarseLocationUpdate() {
        this.zeroCoded = false
        this.Index_Field = Index()
    }

    fun CalcPayloadSize(): Int {
        return (this.Location_Fields.size() * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size() * 16)
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCoarseLocationUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((byte) -1)
        byteBuffer.put((byte) 6)
        byteBuffer.put((this as byte).Location_Fields.size())
        for (Location location : this.Location_Fields) {
            packByte(byteBuffer, (location as byte).X)
            packByte(byteBuffer, (location as byte).Y)
            packByte(byteBuffer, (location as byte).Z)
        }
        packShort(byteBuffer, (this as short).Index_Field.You)
        packShort(byteBuffer, (this as short).Index_Field.Prey)
        byteBuffer.put((this as byte).AgentData_Fields.size())
        for (AgentData agentData : this.AgentData_Fields) {
            packUUID(byteBuffer, agentData.AgentID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Location location = Location()
            location.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Z = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.Location_Fields.add(location)
        }
        this.Index_Field.You = unpackShort(byteBuffer)
        this.Index_Field.Prey = unpackShort(byteBuffer)
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            AgentData agentData = AgentData()
            agentData.AgentID = unpackUUID(byteBuffer)
            this.AgentData_Fields.add(agentData)
        }
    }
}
