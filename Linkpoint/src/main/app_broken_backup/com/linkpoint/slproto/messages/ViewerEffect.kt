package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ViewerEffect : SLMessage {
    AgentData AgentData_Field
    ArrayList<Effect> Effect_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class Effect {
        UUID AgentID
        ByteArray Color
        Float Duration
        UUID ID
        Int Type
        ByteArray TypeData
    }

    ViewerEffect() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 35
        Iterator<T> it = this.Effect_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as Effect).next()).TypeData.size + 42 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleViewerEffect(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).Effect_Fields.size())
        for (Effect effect : this.Effect_Fields) {
            packUUID(byteBuffer, effect.ID)
            packUUID(byteBuffer, effect.AgentID)
            packByte(byteBuffer, (effect as Byte).Type)
            packFloat(byteBuffer, effect.Duration)
            packFixed(byteBuffer, effect.Color, 4)
            packVariable(byteBuffer, effect.TypeData, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Effect effect = Effect()
            effect.ID = unpackUUID(byteBuffer)
            effect.AgentID = unpackUUID(byteBuffer)
            effect.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            effect.Duration = unpackFloat(byteBuffer)
            effect.Color = unpackFixed(byteBuffer, 4)
            effect.TypeData = unpackVariable(byteBuffer, 1)
            this.Effect_Fields.add(effect)
        }
    }
}
