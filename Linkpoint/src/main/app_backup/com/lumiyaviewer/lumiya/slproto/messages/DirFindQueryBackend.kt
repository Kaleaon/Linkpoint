package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DirFindQueryBackend : SLMessage {
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
        var QueryStart: Int = 0
        var QueryText: ByteArray = ByteArray(0)
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 17 + 4 + 4 + 4 + 1 + 20
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirFindQueryBackend(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(32.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        packVariable(byteBuffer, this.QueryData_Field.QueryText, 1)
        packInt(byteBuffer, this.QueryData_Field.QueryFlags)
        packInt(byteBuffer, this.QueryData_Field.QueryStart)
        packInt(byteBuffer, this.QueryData_Field.EstateID)
        packBoolean(byteBuffer, this.QueryData_Field.Godlike)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryText = unpackVariable(byteBuffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(byteBuffer)
        this.QueryData_Field.QueryStart = unpackInt(byteBuffer)
        this.QueryData_Field.EstateID = unpackInt(byteBuffer)
        this.QueryData_Field.Godlike = unpackBoolean(byteBuffer)
    }
}
