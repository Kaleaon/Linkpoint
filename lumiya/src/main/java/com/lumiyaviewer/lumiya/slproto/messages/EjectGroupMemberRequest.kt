package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class EjectGroupMemberRequest : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var EjectData_Fields: ArrayList<EjectData> = ArrayList()

    class AgentData {
        var AgentID: UUID = UUID(0, 0)
        var GroupID: UUID = UUID(0, 0)
    }

    class EjectData {
        var EjecteeID: UUID = UUID(0, 0)
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 32 + 1 + (EjectData_Fields.size * 16)
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEjectGroupMemberRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(71.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.GroupID)
        buffer.put(this.EjectData_Fields.size.toByte())
        for (data in this.EjectData_Fields) {
            packUUID(buffer, data.EjecteeID)
        }
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.GroupID = unpackUUID(buffer)
        val count = buffer.get().toInt() and 0xFF
        for (i in 0 until count) {
            val data = EjectData()
            data.EjecteeID = unpackUUID(buffer)
            this.EjectData_Fields.add(data)
        }
    }
}
