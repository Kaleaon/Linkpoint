package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirEventsReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()
    var QueryReplies_Fields: ArrayList<QueryReplies> = ArrayList()
    var StatusData_Fields: ArrayList<StatusData> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    class QueryReplies {
        var Date: ByteArray = ByteArray(0)
        var EventFlags: Int = 0
        var EventID: Int = 0
        var Name: ByteArray = ByteArray(0)
        var OwnerID: UUID = UUIDPool.ZeroUUID
        var UnixTime: Int = 0
    }

    class StatusData {
        var Status: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 37
        for (reply in QueryReplies_Fields) {
            size += reply.Date.size + reply.Name.size + 17 + 4 + 1 + 4 + 4
        }
        size += 1 + (StatusData_Fields.size * 4)
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirEventsReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(37.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put(this.QueryReplies_Fields.size.toByte())
        for (reply in this.QueryReplies_Fields) {
            packUUID(byteBuffer, reply.OwnerID)
            packVariable(byteBuffer, reply.Name, 1)
            packInt(byteBuffer, reply.EventID)
            packVariable(byteBuffer, reply.Date, 1)
            packInt(byteBuffer, reply.UnixTime)
            packInt(byteBuffer, reply.EventFlags)
        }
        byteBuffer.put(this.StatusData_Fields.size.toByte())
        for (status in this.StatusData_Fields) {
            packInt(byteBuffer, status.Status)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val reply = QueryReplies()
            reply.OwnerID = unpackUUID(byteBuffer)
            reply.Name = unpackVariable(byteBuffer, 1)
            reply.EventID = unpackInt(byteBuffer)
            reply.Date = unpackVariable(byteBuffer, 1)
            reply.UnixTime = unpackInt(byteBuffer)
            reply.EventFlags = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(reply)
        }
        val b2 = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b2) {
            val status = StatusData()
            status.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(status)
        }
    }
}
