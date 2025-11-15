package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimStatus(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put(Ascii.FF)
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptAgents)
        packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptTasks)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.SimStatusData_Field.CanAcceptAgents = unpackBoolean(byteBuffer)
        this.SimStatusData_Field.CanAcceptTasks = unpackBoolean(byteBuffer)
    }
}
