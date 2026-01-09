package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class PlacesReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<QueryData> QueryData_Fields = ArrayList<>()
    TransactionData TransactionData_Field

    class AgentData {
        UUID AgentID
        UUID QueryID
    }

    class QueryData {
        Int ActualArea
        Int BillableArea
        ByteArray Desc
        Float Dwell
        Int Flags
        Float GlobalX
        Float GlobalY
        Float GlobalZ
        ByteArray Name
        UUID OwnerID
        Int Price
        ByteArray SimName
        UUID SnapshotID
    }

    class TransactionData {
        UUID TransactionID
    }

    PlacesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 53
        Iterator<T> it = this.QueryData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            QueryData queryData = (it as QueryData).next()
            i = queryData.SimName.size + queryData.Name.size + 17 + 1 + queryData.Desc.size + 4 + 4 + 1 + 4 + 4 + 4 + 1 + 16 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandlePlacesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.RS)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.QueryID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        byteBuffer.put((this as Byte).QueryData_Fields.size())
        for (QueryData queryData : this.QueryData_Fields) {
            packUUID(byteBuffer, queryData.OwnerID)
            packVariable(byteBuffer, queryData.Name, 1)
            packVariable(byteBuffer, queryData.Desc, 1)
            packInt(byteBuffer, queryData.ActualArea)
            packInt(byteBuffer, queryData.BillableArea)
            packByte(byteBuffer, (queryData as Byte).Flags)
            packFloat(byteBuffer, queryData.GlobalX)
            packFloat(byteBuffer, queryData.GlobalY)
            packFloat(byteBuffer, queryData.GlobalZ)
            packVariable(byteBuffer, queryData.SimName, 1)
            packUUID(byteBuffer, queryData.SnapshotID)
            packFloat(byteBuffer, queryData.Dwell)
            packInt(byteBuffer, queryData.Price)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.QueryID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
