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

    Int CalcPayloadSize() {
        return (this.Location_Fields.size() * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size() * 16)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCoarseLocationUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1)
        byteBuffer.put((byte) 6)
        byteBuffer.put((byte) this.Location_Fields.size())
        for (Location location : this.Location_Fields) {
            packByte(byteBuffer, (byte) location.X)
            packByte(byteBuffer, (byte) location.Y)
            packByte(byteBuffer, (byte) location.Z)
        }
        packShort(byteBuffer, (short) this.Index_Field.You)
        packShort(byteBuffer, (short) this.Index_Field.Prey)
        byteBuffer.put((byte) this.AgentData_Fields.size())
        for (AgentData agentData : this.AgentData_Fields) {
            packUUID(byteBuffer, agentData.AgentID)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Location location = Location()
            location.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Z = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.Location_Fields.add(location)
        }
        this.Index_Field.You = unpackShort(byteBuffer)
        this.Index_Field.Prey = unpackShort(byteBuffer)
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            AgentData agentData = AgentData()
            agentData.AgentID = unpackUUID(byteBuffer)
            this.AgentData_Fields.add(agentData)
        }
    }
}
