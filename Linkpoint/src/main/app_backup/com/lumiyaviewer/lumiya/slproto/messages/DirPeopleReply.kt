package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirPeopleReply : SLMessage {
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
        var AgentID: UUID = UUIDPool.ZeroUUID
        var FirstName: ByteArray = ByteArray(0)
        var GroupName: ByteArray = ByteArray(0)
        var LastName: ByteArray = ByteArray(0)
        var Online: Boolean = false
        var Reputation: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 33
        for (reply in QueryReplies_Fields) {
            size += reply.FirstName.size + 1 + reply.LastName.size + 1 + reply.GroupName.size + 1 + 16 + 1 + 4
        }
        return size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPeopleReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(30.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put(this.QueryReplies_Fields.size.toByte())
        for (reply in this.QueryReplies_Fields) {
            packUUID(byteBuffer, reply.AgentID)
            packVariable(byteBuffer, reply.FirstName, 1)
            packVariable(byteBuffer, reply.LastName, 1)
            packVariable(byteBuffer, reply.GroupName, 1)
            packBoolean(byteBuffer, reply.Online)
            packInt(byteBuffer, reply.Reputation)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val reply = QueryReplies()
            reply.AgentID = unpackUUID(byteBuffer)
            reply.FirstName = unpackVariable(byteBuffer, 1)
            reply.LastName = unpackVariable(byteBuffer, 1)
            reply.GroupName = unpackVariable(byteBuffer, 1)
            reply.Online = unpackBoolean(byteBuffer)
            reply.Reputation = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(reply)
        }
    }
}
