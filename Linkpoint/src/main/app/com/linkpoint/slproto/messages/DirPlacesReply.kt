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
        byte[] Name
        UUID ParcelID
    }

    class StatusData {
        Int Status
    }

    DirPlacesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int size = (this.QueryData_Fields.size() * 16) + 21 + 1
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            Int i = size
            if (!it.hasNext()) {
                return i + 1 + (this.StatusData_Fields.size() * 4)
            }
            size = ((QueryReplies) it.next()).Name.length + 17 + 1 + 1 + 4 + i
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPlacesReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 35)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((byte) this.QueryData_Fields.size())
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.QueryID)
        }
        byteBuffer.put((byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ParcelID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packBoolean(byteBuffer, queryReplies.ForSale)
            packBoolean(byteBuffer, queryReplies.Auction)
            packFloat(byteBuffer, queryReplies.Dwell)
        }
        byteBuffer.put((byte) this.StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            QueryData queryData = QueryData()
            queryData.QueryID = unpackUUID(byteBuffer)
            this.QueryData_Fields.add(queryData)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ParcelID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ForSale = unpackBoolean(byteBuffer)
            queryReplies.Auction = unpackBoolean(byteBuffer)
            queryReplies.Dwell = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i3 = 0; i3 < b3; i3++) {
            StatusData statusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
