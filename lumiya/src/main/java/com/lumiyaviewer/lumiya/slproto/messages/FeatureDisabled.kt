package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FeatureDisabled : SLMessage {
    FailureInfo FailureInfo_Field = FailureInfo()

    class FailureInfo {
        UUID AgentID
        byte[] ErrorMessage
        UUID TransactionID
    }

    FeatureDisabled() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.FailureInfo_Field.ErrorMessage.length + 1 + 16 + 16 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFeatureDisabled(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 19)
        packVariable(byteBuffer, this.FailureInfo_Field.ErrorMessage, 1)
        packUUID(byteBuffer, this.FailureInfo_Field.AgentID)
        packUUID(byteBuffer, this.FailureInfo_Field.TransactionID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.FailureInfo_Field.ErrorMessage = unpackVariable(byteBuffer, 1)
        this.FailureInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.FailureInfo_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
