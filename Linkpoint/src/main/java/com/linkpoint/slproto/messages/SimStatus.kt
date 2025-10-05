package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimStatus : SLMessage() {
    val SimStatusData_Field = SimStatusData()

    class SimStatusData {
        var CanAcceptAgents: Boolean = false
        var CanAcceptTasks: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 4

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSimStatus(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put((-1).toByte())
        buffer.put(Ascii.FF)
        packBoolean(buffer, SimStatusData_Field.CanAcceptAgents)
        packBoolean(buffer, SimStatusData_Field.CanAcceptTasks)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        SimStatusData_Field.CanAcceptAgents = unpackBoolean(buffer)
        SimStatusData_Field.CanAcceptTasks = unpackBoolean(buffer)
    }
}