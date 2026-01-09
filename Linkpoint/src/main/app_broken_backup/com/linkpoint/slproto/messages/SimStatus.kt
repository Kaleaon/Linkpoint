package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimStatus : SLMessage {
    SimStatusData SimStatusData_Field = SimStatusData()

    class SimStatusData {
        Boolean CanAcceptAgents
        Boolean CanAcceptTasks
    }

    SimStatus() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSimStatus(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((Byte) -1)
        byteBuffer.put(Ascii.FF)
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptAgents)
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptTasks)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.SimStatusData_Field.CanAcceptAgents = unpackBoolean(byteBuffer)
        this.SimStatusData_Field.CanAcceptTasks = unpackBoolean(byteBuffer)
    }
}
