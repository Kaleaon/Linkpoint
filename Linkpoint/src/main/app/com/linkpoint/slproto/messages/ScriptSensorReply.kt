package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ScriptSensorReply : SLMessage {
    Requester Requester_Field
    ArrayList<SensedData> SensedData_Fields = ArrayList<>()

    class Requester {
        UUID SourceID
    }

    class SensedData {
        UUID GroupID
        ByteArray Name
        UUID ObjectID
        UUID OwnerID
        LLVector3 Position
        Float Range
        LLQuaternion Rotation
        Int Type
        LLVector3 Velocity
    }

    ScriptSensorReply() {
        this.zeroCoded = true
        this.Requester_Field = Requester()
    }

    fun CalcPayloadSize(): Int {
        Int i = 21
        Iterator<T> it = this.SensedData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as SensedData).next()).Name.size + 85 + 4 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptSensorReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -8)
        packUUID(byteBuffer, this.Requester_Field.SourceID)
        byteBuffer.put((this as Byte).SensedData_Fields.size())
        for (SensedData sensedData : this.SensedData_Fields) {
            packUUID(byteBuffer, sensedData.ObjectID)
            packUUID(byteBuffer, sensedData.OwnerID)
            packUUID(byteBuffer, sensedData.GroupID)
            packLLVector3(byteBuffer, sensedData.Position)
            packLLVector3(byteBuffer, sensedData.Velocity)
            packLLQuaternion(byteBuffer, sensedData.Rotation)
            packVariable(byteBuffer, sensedData.Name, 1)
            packInt(byteBuffer, sensedData.Type)
            packFloat(byteBuffer, sensedData.Range)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Requester_Field.SourceID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            SensedData sensedData = SensedData()
            sensedData.ObjectID = unpackUUID(byteBuffer)
            sensedData.OwnerID = unpackUUID(byteBuffer)
            sensedData.GroupID = unpackUUID(byteBuffer)
            sensedData.Position = unpackLLVector3(byteBuffer)
            sensedData.Velocity = unpackLLVector3(byteBuffer)
            sensedData.Rotation = unpackLLQuaternion(byteBuffer)
            sensedData.Name = unpackVariable(byteBuffer, 1)
            sensedData.Type = unpackInt(byteBuffer)
            sensedData.Range = unpackFloat(byteBuffer)
            this.SensedData_Fields.add(sensedData)
        }
    }
}
