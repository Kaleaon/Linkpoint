package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class EjectUser : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: UUID = UUID(0, 0)
        var SessionID: UUID = UUID(0, 0)
    }

    class Data {
        var UserID: UUID = UUID(0, 0)
        var Flags: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 48 + 4
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEjectUser(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(141.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.AgentData_Field.SessionID)
        packUUID(buffer, this.Data_Field.UserID)
        packInt(buffer, this.Data_Field.Flags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.AgentData_Field.SessionID = unpackUUID(buffer)
        this.Data_Field.UserID = unpackUUID(buffer)
        this.Data_Field.Flags = unpackInt(buffer)
    }
}
