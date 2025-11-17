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

    Int CalcPayloadSize() {
        Int i = 35
        Iterator<T> it = this.Effect_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Effect) it.next()).TypeData.length + 42 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerEffect(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 17)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((Byte) this.Effect_Fields.size())
        for (Effect effect : this.Effect_Fields) {
            packUUID(byteBuffer, effect.ID)
            packUUID(byteBuffer, effect.AgentID)
            packByte(byteBuffer, (Byte) effect.Type)
            packFloat(byteBuffer, effect.Duration)
            packFixed(byteBuffer, effect.Color, 4)
            packVariable(byteBuffer, effect.TypeData, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
