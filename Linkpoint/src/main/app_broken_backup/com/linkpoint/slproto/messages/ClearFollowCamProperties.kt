package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ClearFollowCamProperties : SLMessage {
    ObjectData ObjectData_Field = ObjectData()

    class ObjectData {
        UUID ObjectID
    }

    ClearFollowCamProperties() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleClearFollowCamProperties(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -96)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
    }
}
