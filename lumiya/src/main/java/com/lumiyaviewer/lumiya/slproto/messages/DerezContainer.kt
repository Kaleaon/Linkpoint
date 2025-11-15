package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class DerezContainer : SLMessage {
    Data Data_Field = Data()

    class Data {
        Boolean Delete
        UUID ObjectID
    }

    DerezContainer() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return 21
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDerezContainer(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 104)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packBoolean(byteBuffer, this.Data_Field.Delete)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.Delete = unpackBoolean(byteBuffer)
    }
}
