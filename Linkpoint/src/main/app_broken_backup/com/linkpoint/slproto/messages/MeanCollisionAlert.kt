package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class MeanCollisionAlert : SLMessage {
    ArrayList<MeanCollision> MeanCollision_Fields = ArrayList<>()

    class MeanCollision {
        Float Mag
        UUID Perp
        Int Time
        Int Type
        UUID Victim
    }

    MeanCollisionAlert() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return (this.MeanCollision_Fields.size() * 41) + 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleMeanCollisionAlert(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -120)
        byteBuffer.put((this as Byte).MeanCollision_Fields.size())
        for (MeanCollision meanCollision : this.MeanCollision_Fields) {
            packUUID(byteBuffer, meanCollision.Victim)
            packUUID(byteBuffer, meanCollision.Perp)
            packInt(byteBuffer, meanCollision.Time)
            packFloat(byteBuffer, meanCollision.Mag)
            packByte(byteBuffer, (meanCollision as Byte).Type)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
