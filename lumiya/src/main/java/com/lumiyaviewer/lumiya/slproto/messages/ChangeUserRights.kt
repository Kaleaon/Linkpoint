package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class ChangeUserRights : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Rights_Fields: ArrayList<Rights> = ArrayList()

    class AgentData {
        var AgentID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var SessionID: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
    }

    class Rights {
        var AgentRelated: com.lumiyaviewer.lumiya.slproto.types.UUID = com.lumiyaviewer.lumiya.slproto.types.UUIDPool.ZeroUUID
        var RelatedRights: Int = 0
    }

    override fun CalcPayloadSize(): Int {
        var size = 32
        for (i in Rights_Fields) {
            size += 20
        }
        return size
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // Not implemented
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(this.AgentData_Field.AgentID.data)
        byteBuffer.put(this.AgentData_Field.SessionID.data)
        val size = this.Rights_Fields.size
        byteBuffer.put(size.toByte())
        for (data in this.Rights_Fields) {
            byteBuffer.put(data.AgentRelated.data)
            byteBuffer.putInt(data.RelatedRights)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        this.AgentData_Field.SessionID = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
        val count = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val data = Rights()
            data.AgentRelated = com.lumiyaviewer.lumiya.slproto.types.UUID(byteBuffer)
            data.RelatedRights = byteBuffer.getInt()
            this.Rights_Fields.add(data)
        }
    }
}
