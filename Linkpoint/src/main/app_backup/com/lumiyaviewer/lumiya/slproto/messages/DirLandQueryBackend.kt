package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DirLandQueryBackend : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryFlags: Int = 0
        var QueryID: UUID = UUIDPool.ZeroUUID
        var QueryStart: Int = 0
        var QueryType: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 33
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirLandQueryBackend(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(34.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.QueryType)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.QueryType = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
    }
}
