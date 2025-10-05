package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ScriptSensorReply : SLMessage() {
    public Requester Requester_Field
    public ArrayList<SensedData> SensedData_Fields = ArrayList<>()

    @JvmStatic
    class Requester {
        public UUID SourceID
    }

    @JvmStatic
    class SensedData {
        public UUID GroupID
        public Byte[] Name
        public UUID ObjectID
        public UUID OwnerID
        public LLVector3 Position
        public Float Range
        public LLQuaternion Rotation
        public Int Type
        public LLVector3 Velocity
    }

    public ScriptSensorReply() {
        this.zeroCoded = true
        this.Requester_Field = Requester()
    }

    public Int CalcPayloadSize() {
        Int i = 21
        Iterator<T> it = this.SensedData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((SensedData) it.next()).Name.length + 85 + 4 + 4 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptSensorReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -8)
        packUUID(byteBuffer, this.Requester_Field.SourceID)
        byteBuffer.put((Byte) this.SensedData_Fields.size())
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Requester_Field.SourceID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
