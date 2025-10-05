package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirPlacesReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<QueryData> QueryData_Fields = ArrayList<>()
    public ArrayList<QueryReplies> QueryReplies_Fields = ArrayList<>()
    public ArrayList<StatusData> StatusData_Fields = ArrayList<>()

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
        public Boolean Auction
        public Float Dwell
        public Boolean ForSale
        public Byte[] Name
        public UUID ParcelID
    }

    @JvmStatic
    class StatusData {
        public Int Status
    }

    public DirPlacesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
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

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirPlacesReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 35)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((Byte) this.QueryData_Fields.size())
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.QueryID)
        }
        byteBuffer.put((Byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ParcelID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packBoolean(byteBuffer, queryReplies.ForSale)
            packBoolean(byteBuffer, queryReplies.Auction)
            packFloat(byteBuffer, queryReplies.Dwell)
        }
        byteBuffer.put((Byte) this.StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            QueryData queryData = QueryData()
            queryData.QueryID = unpackUUID(byteBuffer)
            this.QueryData_Fields.add(queryData)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ParcelID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ForSale = unpackBoolean(byteBuffer)
            queryReplies.Auction = unpackBoolean(byteBuffer)
            queryReplies.Dwell = unpackFloat(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        Byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i3 = 0; i3 < b3; i3++) {
            StatusData statusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
