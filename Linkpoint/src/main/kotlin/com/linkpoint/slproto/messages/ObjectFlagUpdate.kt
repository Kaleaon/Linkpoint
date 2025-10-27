package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectFlagUpdate : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ExtraPhysics> ExtraPhysics_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public Boolean CastsShadows
        public Boolean IsPhantom
        public Boolean IsTemporary
        public Int ObjectLocalID
        public UUID SessionID
        public Boolean UsePhysics
    }

    @JvmStatic
    class ExtraPhysics {
        public Float Density
        public Float Friction
        public Float GravityMultiplier
        public Int PhysicsShapeType
        public Float Restitution
    }

    public ObjectFlagUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.ExtraPhysics_Fields.size() * 17) + 45
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleObjectFlagUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 94)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.ObjectLocalID)
        packBoolean(byteBuffer, this.AgentData_Field.UsePhysics)
        packBoolean(byteBuffer, this.AgentData_Field.IsTemporary)
        packBoolean(byteBuffer, this.AgentData_Field.IsPhantom)
        packBoolean(byteBuffer, this.AgentData_Field.CastsShadows)
        byteBuffer.put((Byte) this.ExtraPhysics_Fields.size())
        for (ExtraPhysics extraPhysics : this.ExtraPhysics_Fields) {
            packByte(byteBuffer, (Byte) extraPhysics.PhysicsShapeType)
            packFloat(byteBuffer, extraPhysics.Density)
            packFloat(byteBuffer, extraPhysics.Friction)
            packFloat(byteBuffer, extraPhysics.Restitution)
            packFloat(byteBuffer, extraPhysics.GravityMultiplier)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.ObjectLocalID = unpackInt(byteBuffer)
        this.AgentData_Field.UsePhysics = unpackBoolean(byteBuffer)
        this.AgentData_Field.IsTemporary = unpackBoolean(byteBuffer)
        this.AgentData_Field.IsPhantom = unpackBoolean(byteBuffer)
        this.AgentData_Field.CastsShadows = unpackBoolean(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val extraPhysics: ExtraPhysics = ExtraPhysics()
            extraPhysics.PhysicsShapeType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            extraPhysics.Density = unpackFloat(byteBuffer)
            extraPhysics.Friction = unpackFloat(byteBuffer)
            extraPhysics.Restitution = unpackFloat(byteBuffer)
            extraPhysics.GravityMultiplier = unpackFloat(byteBuffer)
            this.ExtraPhysics_Fields.add(extraPhysics)
        }
    }
}
