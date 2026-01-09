package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirPlacesReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<QueryData> QueryData_Fields = ArrayList<>()
    ArrayList<QueryReplies> QueryReplies_Fields = ArrayList<>()
    ArrayList<StatusData> StatusData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class QueryData {
        UUID QueryID
    }

    class QueryReplies {
        Boolean Auction
        float Dwell
        Boolean ForSale
        ByteArray Name
        UUID ParcelID
    }

    class StatusData {
        Int Status
    }

    DirPlacesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        var size: Int = (this.QueryData_Fields.size() * 16) + 21 + 1
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            var i: Int = size
            if (!it.hasNext()) {
                return i + 1 + (this.StatusData_Fields.size() * 4)
            }
            size = ((it as QueryReplies).next()).Name.size + 17 + 1 + 1 + 4 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleDirPlacesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 35)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((this as byte).QueryData_Fields.size())
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.QueryID)
        }
        byteBuffer.put((this as byte).QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ParcelID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packBoolean(byteBuffer, queryReplies.ForSale)
            packBoolean(byteBuffer, queryReplies.Auction)
            packFloat(byteBuffer, queryReplies.Dwell)
        }
        byteBuffer.put((this as byte).StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            QueryData queryData = QueryData()
            queryData.QueryID = unpackUUID(byteBuffer)
            this.QueryData_Fields.add(queryData)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ParcelID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ForSale = unpackBoolean(byteBuffer)
            queryReplies.Auction = unpackBoolean(byteBuffer)
            queryReplies.Dwell = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i3 in 0 until b3) {
            StatusData statusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
