package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ViewerEffect : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<Effect> Effect_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class Effect {
        public UUID AgentID
        public ByteArray Color
        public Float Duration
        public UUID ID
        public Int Type
        public ByteArray TypeData
    }

    public ViewerEffect() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 35
        val it: Iterator<T> = this.Effect_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((Effect) it.next()).TypeData.length + 42 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleViewerEffect(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val effect: Effect = Effect()
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
