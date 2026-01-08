package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirLandReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryData_Field: QueryData = QueryData()
    var QueryReplies_Fields: ArrayList<QueryReplies> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryData {
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    class QueryReplies {
        var ActualArea: Int = 0
        var Auction: Boolean = false
        var ForSale: Boolean = false
        var Name: ByteArray = ByteArray(0)
        var ParcelID: UUID = UUIDPool.ZeroUUID
        var SalePrice: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 33
        for (reply in QueryReplies_Fields) {
            size += reply.Name.size + 1 + 4 + 1 + 1 + 16 + 4
        }
        return size
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirLandReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(35.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put(this.QueryReplies_Fields.size.toByte())
        for (reply in this.QueryReplies_Fields) {
            packUUID(byteBuffer, reply.ParcelID)
            packVariable(byteBuffer, reply.Name, 1)
            packInt(byteBuffer, reply.ActualArea)
            packInt(byteBuffer, reply.SalePrice)
            packBoolean(byteBuffer, reply.Auction)
            packBoolean(byteBuffer, reply.ForSale)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val reply = QueryReplies()
            reply.ParcelID = unpackUUID(byteBuffer)
            reply.Name = unpackVariable(byteBuffer, 1)
            reply.ActualArea = unpackInt(byteBuffer)
            reply.SalePrice = unpackInt(byteBuffer)
            reply.Auction = unpackBoolean(byteBuffer)
            reply.ForSale = unpackBoolean(byteBuffer)
            this.QueryReplies_Fields.add(reply)
        }
    }
}
