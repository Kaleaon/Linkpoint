package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class TeleportFailed : SLMessage {
    ArrayList<AlertInfo> AlertInfo_Fields = ArrayList<>()
    Info Info_Field

    class AlertInfo {
        ByteArray ExtraParams
        ByteArray Message
    }

    class Info {
        UUID AgentID
        ByteArray Reason
    }

    TeleportFailed() {
        this.zeroCoded = false
        this.Info_Field = Info()
    }

    fun CalcPayloadSize(): Int {
        Int length = this.Info_Field.Reason.size + 17 + 4 + 1
        Iterator<T> it = this.AlertInfo_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            AlertInfo alertInfo = (it as AlertInfo).next()
            length = alertInfo.ExtraParams.size + alertInfo.Message.size + 1 + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTeleportFailed(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 74)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packVariable(byteBuffer, this.Info_Field.Reason, 1)
        byteBuffer.put((this as Byte).AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.Reason = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AlertInfo alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}
