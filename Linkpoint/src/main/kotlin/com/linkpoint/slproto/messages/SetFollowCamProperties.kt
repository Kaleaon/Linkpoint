package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SetFollowCamProperties : SLMessage() {
    public ArrayList<CameraProperty> CameraProperty_Fields = ArrayList<>()
    public ObjectData ObjectData_Field

    @JvmStatic
    class CameraProperty {
        public Int Type
        public Float Value
    }

    @JvmStatic
    class ObjectData {
        public UUID ObjectID
    }

    public SetFollowCamProperties() {
        this.zeroCoded = false
        this.ObjectData_Field = ObjectData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.CameraProperty_Fields.size() * 8) + 21
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleSetFollowCamProperties(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -97)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        byteBuffer.put((Byte) this.CameraProperty_Fields.size())
        for (CameraProperty cameraProperty : this.CameraProperty_Fields) {
            packInt(byteBuffer, cameraProperty.Type)
            packFloat(byteBuffer, cameraProperty.Value)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val cameraProperty: CameraProperty = CameraProperty()
            cameraProperty.Type = unpackInt(byteBuffer)
            cameraProperty.Value = unpackFloat(byteBuffer)
            this.CameraProperty_Fields.add(cameraProperty)
        }
    }
}
