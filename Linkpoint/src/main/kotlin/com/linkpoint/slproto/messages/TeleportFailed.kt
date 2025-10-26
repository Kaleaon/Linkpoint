package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class TeleportFailed : SLMessage() {
    public ArrayList<AlertInfo> AlertInfo_Fields = ArrayList<>()
    public Info Info_Field

    @JvmStatic
    class AlertInfo {
        public ByteArray ExtraParams
        public ByteArray Message
    }

    @JvmStatic
    class Info {
        public UUID AgentID
        public ByteArray Reason
    }

    public TeleportFailed() {
        this.zeroCoded = false
        this.Info_Field = Info()
    }

    public fun CalcPayloadSize(): Int {
        val length: Int = this.Info_Field.Reason.length + 17 + 4 + 1
        val it: Iterator<T> = this.AlertInfo_Fields.iterator()
        while (true) {
            val i: Int = length
            if (!it.hasNext()) {
                return i
            }
            val alertInfo: AlertInfo = (AlertInfo) it.next()
            length = alertInfo.ExtraParams.length + alertInfo.Message.length + 1 + 1 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleTeleportFailed(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 74)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packVariable(byteBuffer, this.Info_Field.Reason, 1)
        byteBuffer.put((Byte) this.AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.Reason = unpackVariable(byteBuffer, 1)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val alertInfo: AlertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}
