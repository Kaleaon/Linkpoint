package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class PayPriceReply : SLMessage() {
    public ArrayList<ButtonData> ButtonData_Fields = ArrayList<>()
    public ObjectData ObjectData_Field

    @JvmStatic
    class ButtonData {
        public Int PayButton
    }

    @JvmStatic
    class ObjectData {
        public Int DefaultPayPrice
        public UUID ObjectID
    }

    public PayPriceReply() {
        this.zeroCoded = false
        this.ObjectData_Field = ObjectData()
    }

    public Int CalcPayloadSize() {
        return (this.ButtonData_Fields.size() * 4) + 25
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandlePayPriceReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
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
