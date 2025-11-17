package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class AbortXfer : SLMessage {
    XferID XferID_Field = XferID()

    class XferID {
        Long ID
        Int Result
    }

    AbortXfer() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 16
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAbortXfer(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -99)
        packLong(byteBuffer, this.XferID_Field.ID)
        packInt(byteBuffer, this.XferID_Field.Result)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.XferID_Field.ID = unpackLong(byteBuffer)
        this.XferID_Field.Result = unpackInt(byteBuffer)
    }
}
