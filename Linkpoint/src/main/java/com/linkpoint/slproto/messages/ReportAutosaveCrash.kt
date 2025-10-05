package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ReportAutosaveCrash : SLMessage() {
    val AutosaveData_Field = AutosaveData()

    class AutosaveData {
        var PID: Int = 0
        var Status: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 12

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleReportAutosaveCrash(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(Byte.MIN_VALUE)
        packInt(buffer, AutosaveData_Field.PID)
        packInt(buffer, AutosaveData_Field.Status)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AutosaveData_Field.PID = unpackInt(buffer)
        AutosaveData_Field.Status = unpackInt(buffer)
    }
}