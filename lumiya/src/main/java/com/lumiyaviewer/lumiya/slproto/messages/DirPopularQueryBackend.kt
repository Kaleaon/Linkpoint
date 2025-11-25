package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DirPopularQueryBackend : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var EstateID: Int = 0
        var Godlike: Boolean = false
        var QueryFlags: Int = 0
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 45
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPopularQueryBackend(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(52.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.QueryData_Field.QueryID)
        packInt(buffer, this.QueryData_Field.QueryFlags)
        packInt(buffer, this.QueryData_Field.EstateID)
        packBoolean(buffer, this.QueryData_Field.Godlike)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.QueryData_Field.QueryID = unpackUUID(buffer)
        this.QueryData_Field.QueryFlags = unpackInt(buffer)
        this.QueryData_Field.EstateID = unpackInt(buffer)
        this.QueryData_Field.Godlike = unpackBoolean(buffer)
    }
}
