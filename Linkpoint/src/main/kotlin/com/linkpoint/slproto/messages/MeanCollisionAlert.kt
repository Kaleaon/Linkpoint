package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class MeanCollisionAlert : SLMessage() {
    public ArrayList<MeanCollision> MeanCollision_Fields = ArrayList<>()

    @JvmStatic
    class MeanCollision {
        public Float Mag
        public UUID Perp
        public Int Time
        public Int Type
        public UUID Victim
    }

    public MeanCollisionAlert() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return (this.MeanCollision_Fields.size() * 41) + 5
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMeanCollisionAlert(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -120)
        byteBuffer.put((Byte) this.MeanCollision_Fields.size())
        for (MeanCollision meanCollision : this.MeanCollision_Fields) {
            packUUID(byteBuffer, meanCollision.Victim)
            packUUID(byteBuffer, meanCollision.Perp)
            packInt(byteBuffer, meanCollision.Time)
            packFloat(byteBuffer, meanCollision.Mag)
            packByte(byteBuffer, (Byte) meanCollision.Type)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            MeanCollision meanCollision = MeanCollision()
            meanCollision.Victim = unpackUUID(byteBuffer)
            meanCollision.Perp = unpackUUID(byteBuffer)
            meanCollision.Time = unpackInt(byteBuffer)
            meanCollision.Mag = unpackFloat(byteBuffer)
            meanCollision.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.MeanCollision_Fields.add(meanCollision)
        }
    }
}
