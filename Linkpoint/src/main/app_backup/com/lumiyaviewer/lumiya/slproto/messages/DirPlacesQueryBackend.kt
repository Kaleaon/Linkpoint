package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer

class DirPlacesQueryBackend : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var Category: Int = 0
        var EstateID: Int = 0
        var Godlike: Boolean = false
        var QueryFlags: Int = 0
        var QueryID: UUID = UUIDPool.ZeroUUID
        var QueryStart: Int = 0
        var QueryText: ByteArray = ByteArray(0)
        var SimName: ByteArray = ByteArray(0)
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return this.QueryData_Field.QueryText.size + 17 + 4 + 1 + 1 + this.QueryData_Field.SimName.size + 4 + 1 + 4 + 20
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPlacesQueryBackend(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(34.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.QueryData_Field.QueryID)
        packVariable(buffer, this.QueryData_Field.QueryText, 1)
        packInt(buffer, this.QueryData_Field.QueryFlags)
        packByte(buffer, this.QueryData_Field.Category.toByte())
        packVariable(buffer, this.QueryData_Field.SimName, 1)
        packInt(buffer, this.QueryData_Field.EstateID)
        packBoolean(buffer, this.QueryData_Field.Godlike)
        packInt(buffer, this.QueryData_Field.QueryStart)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.QueryData_Field.QueryID = unpackUUID(buffer)
        this.QueryData_Field.QueryText = unpackVariable(buffer, 1)
        this.QueryData_Field.QueryFlags = unpackInt(buffer)
        this.QueryData_Field.Category = unpackByte(buffer).toInt() and 0xFF
        this.QueryData_Field.SimName = unpackVariable(buffer, 1)
        this.QueryData_Field.EstateID = unpackInt(buffer)
        this.QueryData_Field.Godlike = unpackBoolean(buffer)
        this.QueryData_Field.QueryStart = unpackInt(buffer)
    }
}
