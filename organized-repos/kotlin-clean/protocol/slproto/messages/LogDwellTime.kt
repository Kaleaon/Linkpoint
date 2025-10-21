package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LogDwellTime : SLMessage() {
    public DwellInfo DwellInfo_Field = DwellInfo()

    @JvmStatic
    class DwellInfo {
        public UUID AgentID
        public Int AvgAgentsInView
        public Int AvgViewerFPS
        public Float Duration
        public Int RegionX
        public Int RegionY
        public UUID SessionID
        public Byte[] SimName
    }

    public LogDwellTime() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.DwellInfo_Field.SimName.length + 37 + 4 + 4 + 1 + 1 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogDwellTime(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.DC2)
        packUUID(byteBuffer, this.DwellInfo_Field.AgentID)
        packUUID(byteBuffer, this.DwellInfo_Field.SessionID)
        packFloat(byteBuffer, this.DwellInfo_Field.Duration)
        packVariable(byteBuffer, this.DwellInfo_Field.SimName, 1)
        packInt(byteBuffer, this.DwellInfo_Field.RegionX)
        packInt(byteBuffer, this.DwellInfo_Field.RegionY)
        packByte(byteBuffer, (Byte) this.DwellInfo_Field.AvgAgentsInView)
        packByte(byteBuffer, (Byte) this.DwellInfo_Field.AvgViewerFPS)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.DwellInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.DwellInfo_Field.SessionID = unpackUUID(byteBuffer)
        this.DwellInfo_Field.Duration = unpackFloat(byteBuffer)
        this.DwellInfo_Field.SimName = unpackVariable(byteBuffer, 1)
        this.DwellInfo_Field.RegionX = unpackInt(byteBuffer)
        this.DwellInfo_Field.RegionY = unpackInt(byteBuffer)
        this.DwellInfo_Field.AvgAgentsInView = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.DwellInfo_Field.AvgViewerFPS = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
