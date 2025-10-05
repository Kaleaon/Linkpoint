package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class CoarseLocationUpdate : SLMessage() {
    public ArrayList<AgentData> AgentData_Fields = ArrayList<>()
    public Index Index_Field
    public ArrayList<Location> Location_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class Index {
        public Int Prey
        public Int You
    }

    @JvmStatic
    class Location {
        public Int X
        public Int Y
        public Int Z
    }

    public CoarseLocationUpdate() {
        this.zeroCoded = false
        this.Index_Field = Index()
    }

    public Int CalcPayloadSize() {
        return (this.Location_Fields.size() * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size() * 16)
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCoarseLocationUpdate(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 6)
        byteBuffer.put((Byte) this.Location_Fields.size())
        for (Location location : this.Location_Fields) {
            packByte(byteBuffer, (Byte) location.X)
            packByte(byteBuffer, (Byte) location.Y)
            packByte(byteBuffer, (Byte) location.Z)
        }
        packShort(byteBuffer, (Short) this.Index_Field.You)
        packShort(byteBuffer, (Short) this.Index_Field.Prey)
        byteBuffer.put((Byte) this.AgentData_Fields.size())
        for (AgentData agentData : this.AgentData_Fields) {
            packUUID(byteBuffer, agentData.AgentID)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            Location location = Location()
            location.X = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Y = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            location.Z = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.Location_Fields.add(location)
        }
        this.Index_Field.You = unpackShort(byteBuffer)
        this.Index_Field.Prey = unpackShort(byteBuffer)
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            AgentData agentData = AgentData()
            agentData.AgentID = unpackUUID(byteBuffer)
            this.AgentData_Fields.add(agentData)
        }
    }
}
