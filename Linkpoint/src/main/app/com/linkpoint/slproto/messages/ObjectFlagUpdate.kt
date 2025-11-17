package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectFlagUpdate : SLMessage {
    AgentData AgentData_Field
    ArrayList<ExtraPhysics> ExtraPhysics_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        Boolean CastsShadows
        Boolean IsPhantom
        Boolean IsTemporary
        Int ObjectLocalID
        UUID SessionID
        Boolean UsePhysics
    }

    class ExtraPhysics {
        Float Density
        Float Friction
        Float GravityMultiplier
        Int PhysicsShapeType
        Float Restitution
    }

    ObjectFlagUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        return (this.ExtraPhysics_Fields.size() * 17) + 45
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectFlagUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.ObjectLocalID = unpackInt(byteBuffer)
        this.AgentData_Field.UsePhysics = unpackBoolean(byteBuffer)
        this.AgentData_Field.IsTemporary = unpackBoolean(byteBuffer)
        this.AgentData_Field.IsPhantom = unpackBoolean(byteBuffer)
        this.AgentData_Field.CastsShadows = unpackBoolean(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ExtraPhysics extraPhysics = ExtraPhysics()
            extraPhysics.PhysicsShapeType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            extraPhysics.Density = unpackFloat(byteBuffer)
            extraPhysics.Friction = unpackFloat(byteBuffer)
            extraPhysics.Restitution = unpackFloat(byteBuffer)
            extraPhysics.GravityMultiplier = unpackFloat(byteBuffer)
            this.ExtraPhysics_Fields.add(extraPhysics)
        }
    }
}
