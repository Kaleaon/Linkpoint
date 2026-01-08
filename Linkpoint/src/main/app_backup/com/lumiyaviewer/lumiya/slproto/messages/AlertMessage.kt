package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList

class AlertMessage : SLMessage {
    var AlertData_Field: AlertData = AlertData()
    var AlertInfo_Fields: ArrayList<AlertInfo> = ArrayList()

    class AlertData {
        var Message: ByteArray = ByteArray(0)
    }

    class AlertInfo {
        var ExtraParams: ByteArray = ByteArray(0)
        var Message: ByteArray = ByteArray(0)
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        var length = AlertData_Field.Message.size + 1 + 4 + 1
        for (alertInfo in AlertInfo_Fields) {
            length += alertInfo.ExtraParams.size + alertInfo.Message.size + 1 + 1
        }
        return length
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAlertMessage(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put(-122.toByte())
        packVariable(byteBuffer, AlertData_Field.Message, 1)
        byteBuffer.put(AlertInfo_Fields.size.toByte())
        for (alertInfo in AlertInfo_Fields) {
            packVariable(byteBuffer, alertInfo.Message, 1)
            packVariable(byteBuffer, alertInfo.ExtraParams, 1)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AlertData_Field.Message = unpackVariable(byteBuffer, 1)
        val b = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b) {
            val alertInfo = AlertInfo()
            alertInfo.Message = unpackVariable(byteBuffer, 1)
            alertInfo.ExtraParams = unpackVariable(byteBuffer, 1)
            AlertInfo_Fields.add(alertInfo)
        }
    }
}
