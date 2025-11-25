package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirPopularReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()
    var QueryReplies_Fields: ArrayList<QueryReplies> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    class QueryReplies {
        var Dwell: Float = 0f
        var Name: ByteArray = ByteArray(0)
        var ParcelID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 33
        for (reply in QueryReplies_Fields) {
            size += reply.Name.size + 1 + 4 + 16
        }
        return size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPopularReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(53.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.QueryData_Field.QueryID)
        buffer.put(this.QueryReplies_Fields.size.toByte())
        for (reply in this.QueryReplies_Fields) {
            packUUID(buffer, reply.ParcelID)
            packVariable(buffer, reply.Name, 1)
            packFloat(buffer, reply.Dwell)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.QueryData_Field.QueryID = unpackUUID(buffer)
        val count = buffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val reply = QueryReplies()
            reply.ParcelID = unpackUUID(buffer)
            reply.Name = unpackVariable(buffer, 1)
            reply.Dwell = unpackFloat(buffer)
            this.QueryReplies_Fields.add(reply)
        }
    }
}
