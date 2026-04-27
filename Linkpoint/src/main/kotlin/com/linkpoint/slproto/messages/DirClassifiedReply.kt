package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class DirClassifiedReply : SLMessage() {
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
        public Int ClassifiedFlags
        public UUID ClassifiedID
        public Int CreationDate
        public Int ExpirationDate
        public ByteArray Name
        public Int PriceForListing
    }

    @JvmStatic
    class StatusData {
        public Int Status
    }

    public DirClassifiedReply() {
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
            i = ((QueryReplies) it.next()).Name.length + 17 + 1 + 4 + 4 + 4 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleDirClassifiedReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 41)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put((Byte) this.QueryReplies_Fields.size())
        for (QueryReplies queryReplies : this.QueryReplies_Fields) {
            packUUID(byteBuffer, queryReplies.ClassifiedID)
            packVariable(byteBuffer, queryReplies.Name, 1)
            packByte(byteBuffer, (Byte) queryReplies.ClassifiedFlags)
            packInt(byteBuffer, queryReplies.CreationDate)
            packInt(byteBuffer, queryReplies.ExpirationDate)
            packInt(byteBuffer, queryReplies.PriceForListing)
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
            queryReplies.ClassifiedID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            queryReplies.CreationDate = unpackInt(byteBuffer)
            queryReplies.ExpirationDate = unpackInt(byteBuffer)
            queryReplies.PriceForListing = unpackInt(byteBuffer)
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
