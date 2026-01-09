package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirClassifiedReply : SLMessage {
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
        Int ClassifiedFlags
        UUID ClassifiedID
        Int CreationDate
        Int ExpirationDate
        ByteArray Name
        Int PriceForListing
    }

    class StatusData {
        Int Status
    }

    DirClassifiedReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.QueryData_Field = QueryData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 37
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4)
            }
            i = ((it as QueryReplies).next()).Name.size + 17 + 1 + 4 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleDirClassifiedReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 41)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((this as byte).QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ClassifiedID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packByte(byteBuffer, (queryReplies as byte).ClassifiedFlags)
            packInt(byteBuffer, queryReplies.CreationDate)
            packInt(byteBuffer, queryReplies.ExpirationDate)
            packInt(byteBuffer, queryReplies.PriceForListing)
        }
        byteBuffer.put((this as byte).StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ClassifiedID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            queryReplies.CreationDate = unpackInt(byteBuffer)
            queryReplies.ExpirationDate = unpackInt(byteBuffer)
            queryReplies.PriceForListing = unpackInt(byteBuffer)
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
