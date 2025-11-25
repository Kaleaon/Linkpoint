package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirGroupsReply : SLMessage {
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
        var GroupID: UUID = UUIDPool.ZeroUUID
        var GroupName: ByteArray = ByteArray(0)
        var Members: Int = 0
        var SearchOrder: Float = 0f
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 33
        for (reply in QueryReplies_Fields) {
            size += reply.GroupName.size + 1 + 16 + 4 + 4
        }
        return size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirGroupsReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(42.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put(this.QueryReplies_Fields.size.toByte())
        for (reply in this.QueryReplies_Fields) {
            packUUID(byteBuffer, reply.GroupID)
            packVariable(byteBuffer, reply.GroupName, 1)
            packInt(byteBuffer, reply.Members)
            packFloat(byteBuffer, reply.SearchOrder)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val reply = QueryReplies()
            reply.GroupID = unpackUUID(byteBuffer)
            reply.GroupName = unpackVariable(byteBuffer, 1)
            reply.Members = unpackInt(byteBuffer)
            reply.SearchOrder = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(reply)
        }
    }
}
