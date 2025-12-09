package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class PayPriceReply : SLMessage {
    ArrayList<ButtonData> ButtonData_Fields = ArrayList<>()
    ObjectData ObjectData_Field

    class ButtonData {
        Int PayButton
    }

    class ObjectData {
        Int DefaultPayPrice
        UUID ObjectID
    }

    PayPriceReply() {
        this.zeroCoded = false
        this.ObjectData_Field = ObjectData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ButtonData_Fields.size() * 4) + 25
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandlePayPriceReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -94)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        packInt(byteBuffer, this.ObjectData_Field.DefaultPayPrice)
        byteBuffer.put((this as Byte).ButtonData_Fields.size())
        for (ButtonData buttonData : this.ButtonData_Fields) {
            packInt(byteBuffer, buttonData.PayButton)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ObjectData_Field.DefaultPayPrice = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ButtonData buttonData = ButtonData()
            buttonData.PayButton = unpackInt(byteBuffer)
            this.ButtonData_Fields.add(buttonData)
        }
    }
}
