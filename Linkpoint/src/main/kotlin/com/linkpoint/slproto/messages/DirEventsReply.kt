package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirEventsReply : SLMessage() {
    public AgentData AgentData_Field
    public QueryData QueryData_Field
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
        public ByteArray Date
        public Int EventFlags
        public Int EventID
        public ByteArray Name
        public UUID OwnerID
        public Int UnixTime
    }

    @JvmStatic
    class StatusData {
        public Int Status
    }

    public DirEventsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.QueryData_Field = QueryData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 37
        val it: Iterator<T> = this.QueryReplies_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4)
            }
            val queryReplies: QueryReplies = (QueryReplies) it.next()
            i = queryReplies.Date.length + queryReplies.Name.length + 17 + 4 + 1 + 4 + 4 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleDirEventsReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 37)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((Byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.OwnerID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packInt(byteBuffer, queryReplies.EventID)
            packVariable(byteBuffer, queryReplies.Date, 1)
            packInt(byteBuffer, queryReplies.UnixTime)
            packInt(byteBuffer, queryReplies.EventFlags)
        }
        byteBuffer.put((Byte) this.StatusData_Fields.size())
        for (StatusData statusData : this.StatusData_Fields) {
            packInt(byteBuffer, statusData.Status)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val queryReplies: QueryReplies = QueryReplies()
            queryReplies.OwnerID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.EventID = unpackInt(byteBuffer)
            queryReplies.Date = unpackVariable(byteBuffer, 1)
            queryReplies.UnixTime = unpackInt(byteBuffer)
            queryReplies.EventFlags = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val statusData: StatusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
