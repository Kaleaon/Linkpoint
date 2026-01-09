package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class AlertMessage : SLMessage {
    AlertData AlertData_Field
    ArrayList<AlertInfo> AlertInfo_Fields = ArrayList<>()

    class AlertData {
        ByteArray Message
    }

    class AlertInfo {
        ByteArray ExtraParams
        ByteArray Message
    }

    AlertMessage() {
        this.zeroCoded = false
        this.AlertData_Field = AlertData()
    }

    fun CalcPayloadSize(): Int {
        var length: Int = this.AlertData_Field.Message.size + 1 + 4 + 1
        Iterator<T> it = this.AlertInfo_Fields.iterator()
        while (true) {
            var i: Int = length
            if (!it.hasNext()) {
                return i
            }
            AlertInfo alertInfo = (it as AlertInfo).next()
            length = alertInfo.ExtraParams.size + alertInfo.Message.size + 1 + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleAlertMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -122)
        packVariable(byteBuffer, this.AlertData_Field.Message, 1)
        byteBuffer.put((this as byte).AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AlertInfo alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}
