package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class PlacesReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<QueryData> QueryData_Fields = ArrayList<>()
    public TransactionData TransactionData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID QueryID
    }

    @JvmStatic
    class QueryData {
        public Int ActualArea
        public Int BillableArea
        public ByteArray Desc
        public Float Dwell
        public Int Flags
        public Float GlobalX
        public Float GlobalY
        public Float GlobalZ
        public ByteArray Name
        public UUID OwnerID
        public Int Price
        public ByteArray SimName
        public UUID SnapshotID
    }

    @JvmStatic
    class TransactionData {
        public UUID TransactionID
    }

    public PlacesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
    }

    public Int CalcPayloadSize() {
        Int i = 53
        Iterator<T> it = this.QueryData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            QueryData queryData = (QueryData) it.next()
            i = queryData.SimName.length + queryData.Name.length + 17 + 1 + queryData.Desc.length + 4 + 4 + 1 + 4 + 4 + 4 + 1 + 16 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePlacesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.RS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        byteBuffer.put((Byte) this.QueryData_Fields.size())
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.OwnerID)
            packVariable(byteBuffer, queryData.Name, 1)
            packVariable(byteBuffer, queryData.Desc, 1)
            packInt(byteBuffer, queryData.ActualArea)
            packInt(byteBuffer, queryData.BillableArea)
            packByte(byteBuffer, (Byte) queryData.Flags)
            packFloat(byteBuffer, queryData.GlobalX)
            packFloat(byteBuffer, queryData.GlobalY)
            packFloat(byteBuffer, queryData.GlobalZ)
            packVariable(byteBuffer, queryData.SimName, 1)
            packUUID(byteBuffer, queryData.SnapshotID)
            packFloat(byteBuffer, queryData.Dwell)
            packInt(byteBuffer, queryData.Price)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            QueryData queryData = QueryData()
            queryData.OwnerID = unpackUUID(byteBuffer)
            queryData.Name = unpackVariable(byteBuffer, 1)
            queryData.Desc = unpackVariable(byteBuffer, 1)
            queryData.ActualArea = unpackInt(byteBuffer)
            queryData.BillableArea = unpackInt(byteBuffer)
            queryData.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            queryData.GlobalX = unpackFloat(byteBuffer)
            queryData.GlobalY = unpackFloat(byteBuffer)
            queryData.GlobalZ = unpackFloat(byteBuffer)
            queryData.SimName = unpackVariable(byteBuffer, 1)
            queryData.SnapshotID = unpackUUID(byteBuffer)
            queryData.Dwell = unpackFloat(byteBuffer)
            queryData.Price = unpackInt(byteBuffer)
            this.QueryData_Fields.add(queryData)
        }
    }
}
