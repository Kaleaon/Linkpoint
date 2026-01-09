package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class SimulatorViewerTimeMessage : SLMessage {
    TimeInfo TimeInfo_Field = TimeInfo()

    class TimeInfo {
        Int SecPerDay
        Int SecPerYear
        LLVector3 SunAngVelocity
        LLVector3 SunDirection
        Float SunPhase
        Long UsecSinceStart
    }

    SimulatorViewerTimeMessage() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 48
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSimulatorViewerTimeMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -106)
        packLong(byteBuffer, this.TimeInfo_Field.UsecSinceStart)
        packInt(byteBuffer, this.TimeInfo_Field.SecPerDay)
        packInt(byteBuffer, this.TimeInfo_Field.SecPerYear)
        packLLVector3(byteBuffer, this.TimeInfo_Field.SunDirection)
        packFloat(byteBuffer, this.TimeInfo_Field.SunPhase)
        packLLVector3(byteBuffer, this.TimeInfo_Field.SunAngVelocity)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.TimeInfo_Field.UsecSinceStart = unpackLong(byteBuffer)
        this.TimeInfo_Field.SecPerDay = unpackInt(byteBuffer)
        this.TimeInfo_Field.SecPerYear = unpackInt(byteBuffer)
        this.TimeInfo_Field.SunDirection = unpackLLVector3(byteBuffer)
        this.TimeInfo_Field.SunPhase = unpackFloat(byteBuffer)
        this.TimeInfo_Field.SunAngVelocity = unpackLLVector3(byteBuffer)
    }
}
