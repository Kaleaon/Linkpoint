package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class SimulatorViewerTimeMessage : SLMessage() {
    public TimeInfo TimeInfo_Field = TimeInfo()

    @JvmStatic
    class TimeInfo {
        public Int SecPerDay
        public Int SecPerYear
        public LLVector3 SunAngVelocity
        public LLVector3 SunDirection
        public Float SunPhase
        public Long UsecSinceStart
    }

    public SimulatorViewerTimeMessage() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 48
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorViewerTimeMessage(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.TimeInfo_Field.UsecSinceStart = unpackLong(byteBuffer)
        this.TimeInfo_Field.SecPerDay = unpackInt(byteBuffer)
        this.TimeInfo_Field.SecPerYear = unpackInt(byteBuffer)
        this.TimeInfo_Field.SunDirection = unpackLLVector3(byteBuffer)
        this.TimeInfo_Field.SunPhase = unpackFloat(byteBuffer)
        this.TimeInfo_Field.SunAngVelocity = unpackLLVector3(byteBuffer)
    }
}
