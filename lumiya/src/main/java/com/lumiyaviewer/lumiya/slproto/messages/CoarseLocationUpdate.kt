package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class CoarseLocationUpdate : SLMessage {
    var AgentData_Fields: ArrayList<AgentData> = ArrayList()
    var Index_Field: Index = Index()
    var Location_Fields: ArrayList<Location> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class Index {
        var Prey: Int = 0
        var You: Int = 0
    }

    class Location {
        var X: Int = 0
        var Y: Int = 0
        var Z: Int = 0
    }

    init {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return (this.Location_Fields.size * 3) + 3 + 4 + 1 + (this.AgentData_Fields.size * 16)
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleCoarseLocationUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(-1)
        byteBuffer.put(6)
        byteBuffer.put(this.Location_Fields.size.toByte())
        for (location in this.Location_Fields) {
            packByte(byteBuffer, location.X.toByte())
            packByte(byteBuffer, location.Y.toByte())
            packByte(byteBuffer, location.Z.toByte())
        }
        packShort(byteBuffer, this.Index_Field.You.toShort())
        packShort(byteBuffer, this.Index_Field.Prey.toShort())
        byteBuffer.put(this.AgentData_Fields.size.toByte())
        for (agentData in this.AgentData_Fields) {
            packUUID(byteBuffer, agentData.AgentID)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        val locationCount = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until locationCount) {
            val location = Location()
            location.X = unpackByte(byteBuffer).toInt() and 0xFF
            location.Y = unpackByte(byteBuffer).toInt() and 0xFF
            location.Z = unpackByte(byteBuffer).toInt() and 0xFF
            this.Location_Fields.add(location)
        }
        this.Index_Field.You = unpackShort(byteBuffer).toInt()
        this.Index_Field.Prey = unpackShort(byteBuffer).toInt()
        val agentCount = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until agentCount) {
            val agentData = AgentData()
            agentData.AgentID = unpackUUID(byteBuffer)
            this.AgentData_Fields.add(agentData)
        }
    }
}
