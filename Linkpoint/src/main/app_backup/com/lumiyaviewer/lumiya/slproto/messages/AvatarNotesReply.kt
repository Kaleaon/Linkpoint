package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.UUID

class AvatarNotesReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var Data_Field: Data = Data()

    class AgentData {
        var AgentID: UUID? = null
    }

    class Data {
        var Notes: ByteArray = ByteArray(0)
        var TargetID: UUID? = null
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return Data_Field.Notes.size + 18 + 20
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarNotesReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-80).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, Data_Field.TargetID!!)
        packVariable(byteBuffer, Data_Field.Notes, 2)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        Data_Field.TargetID = unpackUUID(byteBuffer)
        Data_Field.Notes = unpackVariable(byteBuffer, 2)
    }
}
