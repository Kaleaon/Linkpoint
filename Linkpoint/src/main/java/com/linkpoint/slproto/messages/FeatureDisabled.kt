package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FeatureDisabled : SLMessage() {
    val FailureInfo_Field = FailureInfo()

    class FailureInfo {
        lateinit var ErrorMessage: ByteArray
        var AgentID: UUID? = null
        var TransactionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return FailureInfo_Field.ErrorMessage.size + 1 + 16 + 16 + 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleFeatureDisabled(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(19.toByte())
        packVariable(buffer, FailureInfo_Field.ErrorMessage, 1)
        packUUID(buffer, FailureInfo_Field.AgentID)
        packUUID(buffer, FailureInfo_Field.TransactionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        FailureInfo_Field.ErrorMessage = unpackVariable(buffer, 1)
        FailureInfo_Field.AgentID = unpackUUID(buffer)
        FailureInfo_Field.TransactionID = unpackUUID(buffer)
    }
}