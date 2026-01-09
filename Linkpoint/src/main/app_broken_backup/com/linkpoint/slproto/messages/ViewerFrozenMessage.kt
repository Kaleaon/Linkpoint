package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ViewerFrozenMessage : SLMessage {
    FrozenData FrozenData_Field = FrozenData()

    class FrozenData {
        Boolean Data
    }

    ViewerFrozenMessage() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleViewerFrozenMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -119)
        packBoolean(byteBuffer, this.FrozenData_Field.Data)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.FrozenData_Field.Data = unpackBoolean(byteBuffer)
    }
}
