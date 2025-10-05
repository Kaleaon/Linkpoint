package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirPeopleReply : SLMessage() {
    public AgentData AgentData_Field
    public QueryData QueryData_Field
    public ArrayList<QueryReplies> QueryReplies_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class QueryData {
        public UUID QueryID
    }

    @JvmStatic
    class QueryReplies {
        public UUID AgentID
        public Byte[] FirstName
        public Byte[] Group
        public Byte[] LastName
        public Boolean Online
        public Int Reputation
    }

    public DirPeopleReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.QueryData_Field = QueryData()
    }

    public Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            QueryReplies queryReplies = (QueryReplies) it.next()
            i = queryReplies.Group.length + queryReplies.FirstName.length + 17 + 1 + queryReplies.LastName.length + 1 + 1 + 4 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPeopleReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 36)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((Byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.AgentID)
            packVariable(byteBuffer, queryReplies.FirstName, 1)
            packVariable(byteBuffer, queryReplies.LastName, 1)
            packVariable(byteBuffer, queryReplies.Group, 1)
            packBoolean(byteBuffer, queryReplies.Online)
            packInt(byteBuffer, queryReplies.Reputation)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
