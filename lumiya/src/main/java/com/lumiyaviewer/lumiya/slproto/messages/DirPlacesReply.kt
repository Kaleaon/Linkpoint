package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirPlacesReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Fields: ArrayList<QueryData> = ArrayList()
    var QueryReplies_Fields: ArrayList<QueryReplies> = ArrayList()
    var StatusData_Fields: ArrayList<StatusData> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    class QueryReplies {
        var Auction: Boolean = false
        var Dwell: Float = 0f
        var ForSale: Boolean = false
        var Name: ByteArray = ByteArray(0)
        var ParcelID: UUID = UUIDPool.ZeroUUID
        var Status: Int = 0
    }

    class StatusData {
        var Status: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 17 + 1 // AgentData + count bytes
        size += QueryData_Fields.size * 16
        size += 1 // count bytes
        for (reply in QueryReplies_Fields) {
            size += reply.Name.size + 1 + 16 + 4 + 1 + 1 + 4
        }
        size += 1 // count bytes
        size += StatusData_Fields.size * 4
        return size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirPlacesReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        byteBufferPut(buffer, 43.toByte()) // Helper or direct put
        
        packUUID(buffer, this.AgentData_Field.AgentID)
        
        buffer.put(this.QueryData_Fields.size.toByte())
        for (queryData in this.QueryData_Fields) {
            packUUID(buffer, queryData.QueryID)
        }
        
        buffer.put(this.QueryReplies_Fields.size.toByte())
        for (queryReplies in this.QueryReplies_Fields) {
            packUUID(buffer, queryReplies.ParcelID)
            packVariable(buffer, queryReplies.Name, 1)
            packBoolean(buffer, queryReplies.ForSale)
            packBoolean(buffer, queryReplies.Auction)
            packFloat(buffer, queryReplies.Dwell)
            packInt(buffer, queryReplies.Status)
        }
        
        buffer.put(this.StatusData_Fields.size.toByte())
        for (statusData in this.StatusData_Fields) {
            packInt(buffer, statusData.Status)
        }
    }
    
    // Helper to fix byte ambiguity if needed, or just use put(Byte)
    private fun byteBufferPut(buffer: ByteBuffer, b: Byte) {
        buffer.put(b)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        
        val count = buffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val queryData = QueryData()
            queryData.QueryID = unpackUUID(buffer)
            this.QueryData_Fields.add(queryData)
        }
        
        val count2 = buffer.get().toInt() and 0xFF
        for (i in 0 until count2) {
            val queryReplies = QueryReplies()
            queryReplies.ParcelID = unpackUUID(buffer)
            queryReplies.Name = unpackVariable(buffer, 1)
            queryReplies.ForSale = unpackBoolean(buffer)
            queryReplies.Auction = unpackBoolean(buffer)
            queryReplies.Dwell = unpackFloat(buffer)
            queryReplies.Status = unpackInt(buffer)
            this.QueryReplies_Fields.add(queryReplies)
        }
        
        val count3 = buffer.get().toInt() and 0xFF
        for (i in 0 until count3) {
            val statusData = StatusData()
            statusData.Status = unpackInt(buffer)
            this.StatusData_Fields.add(statusData)
        }
    }
}
