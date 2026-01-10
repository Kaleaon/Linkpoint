package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirPopularReply : SLMessage {
    AgentData AgentData_Field
    QueryData QueryData_Field
    ArrayList<QueryReplies> QueryReplies_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class QueryData {
        UUID QueryID
    }

    class QueryReplies {
        float Dwell
        ByteArray Name
        UUID ParcelID
    }

    DirPopularReply() {
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
                return i2
            }
            i = ((it as QueryReplies).next()).Name.size + 17 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDirPopularReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 53)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((this as byte).QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ParcelID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packFloat(byteBuffer, queryReplies.Dwell)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ParcelID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.Dwell = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
    }
}
