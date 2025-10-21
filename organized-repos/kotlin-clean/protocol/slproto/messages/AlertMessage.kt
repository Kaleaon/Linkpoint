package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator

class AlertMessage : SLMessage() {
    public AlertData AlertData_Field
    public ArrayList<AlertInfo> AlertInfo_Fields = ArrayList<>()

    @JvmStatic
    class AlertData {
        public Byte[] Message
    }

    @JvmStatic
    class AlertInfo {
        public Byte[] ExtraParams
        public Byte[] Message
    }

    public AlertMessage() {
        this.zeroCoded = false
        this.AlertData_Field = AlertData()
    }

    public Int CalcPayloadSize() {
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

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAlertMessage(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -122)
        packVariable(byteBuffer, this.AlertData_Field.Message, 1)
        byteBuffer.put((Byte) this.AlertInfo_Fields.size())
        for (AlertInfo alertInfo : this.AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AlertData_Field.Message = unpackVariable(byteBuffer, 1)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            AlertInfo alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            this.AlertInfo_Fields.add(alertInfo)
        }
    }
}
