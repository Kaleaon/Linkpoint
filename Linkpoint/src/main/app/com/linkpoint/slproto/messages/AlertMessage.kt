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
        byte[] Message
    }

    class AlertInfo {
        byte[] ExtraParams
        byte[] Message
    }

    AlertMessage() {
        this.zeroCoded = false
        this.AlertData_Field = AlertData()
    }

    Int CalcPayloadSize() {
        Int length = this.AlertData_Field.Message.length + 1 + 4 + 1
        Iterator<T> it = this.AlertInfo_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            AlertInfo alertInfo = (AlertInfo) it.next()
            length = alertInfo.ExtraParams.length + alertInfo.Message.length + 1 + 1 + i
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAlertMessage(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -122)
        packVariable(byteBuffer, this.AlertData_Field.Message, 1)
        byteBuffer.put((byte) this.AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AlertInfo alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}
