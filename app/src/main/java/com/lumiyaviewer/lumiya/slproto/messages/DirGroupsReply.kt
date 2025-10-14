package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirGroupsReply : SLMessage {
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
        UUID GroupID
        byte[] GroupName
        Int Members
        float SearchOrder
    }

    DirGroupsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.QueryData_Field = QueryData()
    }

    Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((QueryReplies) it.next()).GroupName.length + 17 + 4 + 4 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirGroupsReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 38)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.GroupID)
            packVariable(byteBuffer, queryReplies.GroupName, 1)
            packInt(byteBuffer, queryReplies.Members)
            packFloat(byteBuffer, queryReplies.SearchOrder)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.GroupID = unpackUUID(byteBuffer)
            queryReplies.GroupName = unpackVariable(byteBuffer, 1)
            queryReplies.Members = unpackInt(byteBuffer)
            queryReplies.SearchOrder = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
    }
}
