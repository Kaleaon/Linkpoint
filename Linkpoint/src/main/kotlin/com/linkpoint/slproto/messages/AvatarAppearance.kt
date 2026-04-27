package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarAppearance : SLMessage() {
    public ArrayList<AppearanceData> AppearanceData_Fields = ArrayList<>()
    public ObjectData ObjectData_Field
    public Sender Sender_Field
    public ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()

    @JvmStatic
    class AppearanceData {
        public Int AppearanceVersion
        public Int CofVersion
        public Int Flags
    }

    @JvmStatic
    class ObjectData {
        public ByteArray TextureEntry
    }

    @JvmStatic
    class Sender {
        public UUID ID
        public Boolean IsTrial
    }

    @JvmStatic
    class VisualParam {
        public Int ParamValue
    }

    public AvatarAppearance() {
        this.zeroCoded = true
        this.Sender_Field = Sender()
        this.ObjectData_Field = ObjectData()
    }

    public fun CalcPayloadSize(): Int {
        return this.ObjectData_Field.TextureEntry.length + 2 + 21 + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AppearanceData_Fields.size() * 9)
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarAppearance(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -98)
        packUUID(byteBuffer, this.Sender_Field.ID)
        packBoolean(byteBuffer, this.Sender_Field.IsTrial)
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2)
        byteBuffer.put((Byte) this.VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (Byte) visualParam.ParamValue)
        }
        byteBuffer.put((Byte) this.AppearanceData_Fields.size())
        for (AppearanceData appearanceData : this.AppearanceData_Fields) {
            packByte(byteBuffer, (Byte) appearanceData.AppearanceVersion)
            packInt(byteBuffer, appearanceData.CofVersion)
            packInt(byteBuffer, appearanceData.Flags)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Sender_Field.ID = unpackUUID(byteBuffer)
        this.Sender_Field.IsTrial = unpackBoolean(byteBuffer)
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val visualParam: VisualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val appearanceData: AppearanceData = AppearanceData()
            appearanceData.AppearanceVersion = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            appearanceData.CofVersion = unpackInt(byteBuffer)
            appearanceData.Flags = unpackInt(byteBuffer)
            this.AppearanceData_Fields.add(appearanceData)
        }
    }
}
