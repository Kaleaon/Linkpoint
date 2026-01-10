package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirPeopleReply : SLMessage {
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
        UUID AgentID
        ByteArray FirstName
        ByteArray Group
        ByteArray LastName
        Boolean Online
        Int Reputation
    }

    DirPeopleReply() {
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
            QueryReplies queryReplies = (it as QueryReplies).next()
            i = queryReplies.Group.size + queryReplies.FirstName.size + 17 + 1 + queryReplies.LastName.size + 1 + 1 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDirPeopleReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 36)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((this as byte).QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.AgentID)
            packVariable(byteBuffer, queryReplies.FirstName, 1)
            packVariable(byteBuffer, queryReplies.LastName, 1)
            packVariable(byteBuffer, queryReplies.Group, 1)
            packBoolean(byteBuffer, queryReplies.Online)
            packInt(byteBuffer, queryReplies.Reputation)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.AgentID = unpackUUID(byteBuffer)
            queryReplies.FirstName = unpackVariable(byteBuffer, 1)
            queryReplies.LastName = unpackVariable(byteBuffer, 1)
            queryReplies.Group = unpackVariable(byteBuffer, 1)
            queryReplies.Online = unpackBoolean(byteBuffer)
            queryReplies.Reputation = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
    }
}
