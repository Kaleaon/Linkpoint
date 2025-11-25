package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class DirClassifiedReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var QueryAnswers_Fields: ArrayList<QueryAnswers> = ArrayList()
    var QueryData_Field: QueryData = QueryData()

    class AgentData {
        var AgentID: UUID = UUIDPool.ZeroUUID
    }

    class QueryAnswers {
        var ClassifiedFlags: Int = 0
        var ClassifiedID: UUID = UUIDPool.ZeroUUID
        var CreationDate: Int = 0
        var ExpirationDate: Int = 0
        var Name: ByteArray = ByteArray(0)
        var PriceForListing: Int = 0
    }

    class QueryData {
        var QueryID: UUID = UUIDPool.ZeroUUID
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var size = 33
        for (answer in QueryAnswers_Fields) {
            size += answer.Name.size + 1 + 36
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDirClassifiedReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(40.toByte())
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.QueryData_Field.QueryID)
        byteBuffer.put(this.QueryAnswers_Fields.size.toByte())
        for (answer in this.QueryAnswers_Fields) {
            packUUID(byteBuffer, answer.ClassifiedID)
            packVariable(byteBuffer, answer.Name, 1)
            packInt(byteBuffer, answer.ClassifiedFlags)
            packInt(byteBuffer, answer.CreationDate)
            packInt(byteBuffer, answer.ExpirationDate)
            packInt(byteBuffer, answer.PriceForListing)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer)
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val answer = QueryAnswers()
            answer.ClassifiedID = unpackUUID(byteBuffer)
            answer.Name = unpackVariable(byteBuffer, 1)
            answer.ClassifiedFlags = unpackInt(byteBuffer)
            answer.CreationDate = unpackInt(byteBuffer)
            answer.ExpirationDate = unpackInt(byteBuffer)
            answer.PriceForListing = unpackInt(byteBuffer)
            this.QueryAnswers_Fields.add(answer)
        }
    }
}
