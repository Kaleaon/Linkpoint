package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ScriptDialog : SLMessage {
    ArrayList<Buttons> Buttons_Fields = ArrayList<>()
    Data Data_Field
    ArrayList<OwnerData> OwnerData_Fields = ArrayList<>()

    class Buttons {
        ByteArray ButtonLabel
    }

    class Data {
        Int ChatChannel
        ByteArray FirstName
        UUID ImageID
        ByteArray LastName
        ByteArray Message
        UUID ObjectID
        ByteArray ObjectName
    }

    class OwnerData {
        UUID OwnerID
    }

    ScriptDialog() {
        this.zeroCoded = true
        this.Data_Field = Data()
    }

    fun CalcPayloadSize(): Int {
        Int length = this.Data_Field.FirstName.size + 17 + 1 + this.Data_Field.LastName.size + 1 + this.Data_Field.ObjectName.size + 2 + this.Data_Field.Message.size + 4 + 16 + 4 + 1
        Iterator<T> it = this.Buttons_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i + 1 + (this.OwnerData_Fields.size() * 16)
            }
            length = ((it as Buttons).next()).ButtonLabel.size + 1 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleScriptDialog(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -66)
        packUUID(byteBuffer, this.Data_Field.ObjectID)
        packVariable(byteBuffer, this.Data_Field.FirstName, 1)
        packVariable(byteBuffer, this.Data_Field.LastName, 1)
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1)
        packVariable(byteBuffer, this.Data_Field.Message, 2)
        packInt(byteBuffer, this.Data_Field.ChatChannel)
        packUUID(byteBuffer, this.Data_Field.ImageID)
        byteBuffer.put((this as Byte).Buttons_Fields.size())
        for (Buttons buttons : this.Buttons_Fields) {
            packVariable(byteBuffer, buttons.ButtonLabel, 1)
        }
        byteBuffer.put((this as Byte).OwnerData_Fields.size())
        for (OwnerData ownerData : this.OwnerData_Fields) {
            packUUID(byteBuffer, ownerData.OwnerID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.FirstName = unpackVariable(byteBuffer, 1)
        this.Data_Field.LastName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.Message = unpackVariable(byteBuffer, 2)
        this.Data_Field.ChatChannel = unpackInt(byteBuffer)
        this.Data_Field.ImageID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            Buttons buttons = Buttons()
            buttons.ButtonLabel = unpackVariable(byteBuffer, 1)
            this.Buttons_Fields.add(buttons)
        }
        Byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            OwnerData ownerData = OwnerData()
            ownerData.OwnerID = unpackUUID(byteBuffer)
            this.OwnerData_Fields.add(ownerData)
        }
    }
}
