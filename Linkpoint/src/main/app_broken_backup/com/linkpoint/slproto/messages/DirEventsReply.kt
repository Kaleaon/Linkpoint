package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirEventsReply : SLMessage {
    AgentData AgentData_Field
    QueryData QueryData_Field
    ArrayList<QueryReplies> QueryReplies_Fields = ArrayList<>()
    ArrayList<StatusData> StatusData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class QueryData {
        UUID QueryID
    }

    class QueryReplies {
        ByteArray Date
        Int EventFlags
        Int EventID
        ByteArray Name
        UUID OwnerID
        Int UnixTime
    }

    class StatusData {
        Int Status
    }

    DirEventsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.QueryData_Field = QueryData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 37
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4)
            }
            QueryReplies queryReplies = (it as QueryReplies).next()
            i = queryReplies.Date.size + queryReplies.Name.size + 17 + 4 + 1 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDirEventsReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 37)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((this as byte).QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.OwnerID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packInt(byteBuffer, queryReplies.EventID)
            packVariable(byteBuffer, queryReplies.Date, 1)
            packInt(byteBuffer, queryReplies.UnixTime)
            packInt(byteBuffer, queryReplies.EventFlags)
        }
        byteBuffer.put((this as byte).StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.OwnerID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.EventID = unpackInt(byteBuffer)
            queryReplies.Date = unpackVariable(byteBuffer, 1)
            queryReplies.UnixTime = unpackInt(byteBuffer)
            queryReplies.EventFlags = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            StatusData statusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
