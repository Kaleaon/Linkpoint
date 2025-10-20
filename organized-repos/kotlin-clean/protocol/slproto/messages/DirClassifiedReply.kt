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
        public Byte[] Name
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

    public Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.QueryReplies_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1 + (this.StatusData_Fields.size() * 4)
            }
            i = ((QueryReplies) it.next()).Name.length + 17 + 1 + 4 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDirClassifiedReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
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

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            QueryReplies queryReplies = QueryReplies()
            queryReplies.ClassifiedID = unpackUUID(byteBuffer)
            queryReplies.Name = unpackVariable(byteBuffer, 1)
            queryReplies.ClassifiedFlags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            queryReplies.CreationDate = unpackInt(byteBuffer)
            queryReplies.ExpirationDate = unpackInt(byteBuffer)
            queryReplies.PriceForListing = unpackInt(byteBuffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            StatusData statusData = StatusData()
            statusData.Status = unpackInt(byteBuffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
