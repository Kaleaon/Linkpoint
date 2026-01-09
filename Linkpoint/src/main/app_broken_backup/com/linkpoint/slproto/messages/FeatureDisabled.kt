package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FeatureDisabled : SLMessage {
    FailureInfo FailureInfo_Field = FailureInfo()

    class FailureInfo {
        UUID AgentID
        ByteArray ErrorMessage
        UUID TransactionID
    }

    FeatureDisabled() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.FailureInfo_Field.ErrorMessage.size + 1 + 16 + 16 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleFeatureDisabled(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 19)
        packVariable(byteBuffer, this.FailureInfo_Field.ErrorMessage, 1)
        packUUID(byteBuffer, this.FailureInfo_Field.AgentID)
        packUUID(byteBuffer, this.FailureInfo_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.FailureInfo_Field.ErrorMessage = unpackVariable(byteBuffer, 1)
        this.FailureInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.FailureInfo_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
