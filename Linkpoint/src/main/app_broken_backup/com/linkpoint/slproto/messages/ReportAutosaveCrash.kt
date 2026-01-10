package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ReportAutosaveCrash : SLMessage {
    AutosaveData AutosaveData_Field = AutosaveData()

    class AutosaveData {
        Int PID
        Int Status
    }

    ReportAutosaveCrash() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 12
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleReportAutosaveCrash(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Byte.MIN_VALUE)
        packInt(byteBuffer, this.AutosaveData_Field.PID)
        packInt(byteBuffer, this.AutosaveData_Field.Status)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AutosaveData_Field.PID = unpackInt(byteBuffer)
        this.AutosaveData_Field.Status = unpackInt(byteBuffer)
    }
}
