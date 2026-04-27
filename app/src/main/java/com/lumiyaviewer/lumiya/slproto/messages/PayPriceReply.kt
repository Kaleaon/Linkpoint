package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return (this.ButtonData_Fields.size() * 4) + 25
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePayPriceReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -94)
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID)
        packInt(byteBuffer, this.ObjectData_Field.DefaultPayPrice)
        byteBuffer.put((Byte) this.ButtonData_Fields.size())
        for (ButtonData buttonData : this.ButtonData_Fields) {
            packInt(byteBuffer, buttonData.PayButton)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer)
        this.ObjectData_Field.DefaultPayPrice = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ButtonData buttonData = ButtonData()
            buttonData.PayButton = unpackInt(byteBuffer)
            this.ButtonData_Fields.add(buttonData)
        }
    }
}
