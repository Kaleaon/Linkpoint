package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DirPopularQuery : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
        var SessionID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryFlags: Int = 0
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 56
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPopularQuery(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(51.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        packUUID(buffer, this.QueryData_Field.QueryID)
        packInt(buffer, this.QueryData_Field.QueryFlags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        this.QueryData_Field.QueryID = unpackUUID(buffer)
        this.QueryData_Field.QueryFlags = unpackInt(buffer)
    }
}
