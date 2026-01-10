package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class SetFollowCamProperties : SLMessage {
    ArrayList<CameraProperty> CameraProperty_Fields = ArrayList<>()
    ObjectData ObjectData_Field

    class CameraProperty {
        Int Type
        Float Value
    }

    class ObjectData {
        UUID ObjectID
    }

    SetFollowCamProperties() {
        this.zeroCoded = false
        this.ObjectData_Field = ObjectData()
    }

    fun CalcPayloadSize(): Int {
        return (this.CameraProperty_Fields.size() * 8) + 21
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSetFollowCamProperties(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -97)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        byteBuffer.put((this as Byte).CameraProperty_Fields.size())
        for (CameraProperty cameraProperty : this.CameraProperty_Fields) {
            packInt(byteBuffer, cameraProperty.Type)
            packFloat(byteBuffer, cameraProperty.Value)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            CameraProperty cameraProperty = CameraProperty()
            cameraProperty.Type = unpackInt(byteBuffer)
            cameraProperty.Value = unpackFloat(byteBuffer)
            this.CameraProperty_Fields.add(cameraProperty)
        }
    }
}
