package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MuteListUpdate : SLMessage() {
    val MuteData_Field = MuteData()

    class MuteData {
        var AgentID: UUID? = null
        lateinit var Filename: ByteArray
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return MuteData_Field.Filename.size + 17 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleMuteListUpdate(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(62.toByte())
        packUUID(buffer, MuteData_Field.AgentID)
        packVariable(buffer, MuteData_Field.Filename, 1)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        MuteData_Field.AgentID = unpackUUID(buffer)
        MuteData_Field.Filename = unpackVariable(buffer, 1)
    }
}