package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ScriptDialog : SLMessage() {
    public ArrayList<Buttons> Buttons_Fields = ArrayList<>()
    public Data Data_Field
    public ArrayList<OwnerData> OwnerData_Fields = ArrayList<>()

    @JvmStatic
    class Buttons {
        public ByteArray ButtonLabel
    }

    @JvmStatic
    class Data {
        public Int ChatChannel
        public ByteArray FirstName
        public UUID ImageID
        public ByteArray LastName
        public ByteArray Message
        public UUID ObjectID
        public ByteArray ObjectName
    }

    @JvmStatic
    class OwnerData {
        public UUID OwnerID
    }

    public ScriptDialog() {
        this.zeroCoded = true
        this.Data_Field = Data()
    }

    public fun CalcPayloadSize(): Int {
        val length: Int = this.Data_Field.FirstName.length + 17 + 1 + this.Data_Field.LastName.length + 1 + this.Data_Field.ObjectName.length + 2 + this.Data_Field.Message.length + 4 + 16 + 4 + 1
        val it: Iterator<T> = this.Buttons_Fields.iterator()
        while (true) {
            val i: Int = length
            if (!it.hasNext()) {
                return i + 1 + (this.OwnerData_Fields.size() * 16)
            }
            length = ((Buttons) it.next()).ButtonLabel.length + 1 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleScriptDialog(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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
        byteBuffer.put((Byte) this.Buttons_Fields.size())
        for (Buttons buttons : this.Buttons_Fields) {
            packVariable(byteBuffer, buttons.ButtonLabel, 1)
        }
        byteBuffer.put((Byte) this.OwnerData_Fields.size())
        for (OwnerData ownerData : this.OwnerData_Fields) {
            packUUID(byteBuffer, ownerData.OwnerID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer)
        this.Data_Field.FirstName = unpackVariable(byteBuffer, 1)
        this.Data_Field.LastName = unpackVariable(byteBuffer, 1)
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1)
        this.Data_Field.Message = unpackVariable(byteBuffer, 2)
        this.Data_Field.ChatChannel = unpackInt(byteBuffer)
        this.Data_Field.ImageID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val buttons: Buttons = Buttons()
            buttons.ButtonLabel = unpackVariable(byteBuffer, 1)
            this.Buttons_Fields.add(buttons)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val ownerData: OwnerData = OwnerData()
            ownerData.OwnerID = unpackUUID(byteBuffer)
            this.OwnerData_Fields.add(ownerData)
        }
    }
}
