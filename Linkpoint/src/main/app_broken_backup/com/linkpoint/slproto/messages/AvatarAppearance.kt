package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarAppearance : SLMessage {
    ArrayList<AppearanceData> AppearanceData_Fields = ArrayList<>()
    ObjectData ObjectData_Field
    Sender Sender_Field
    ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()

    class AppearanceData {
        Int AppearanceVersion
        Int CofVersion
        Int Flags
    }

    class ObjectData {
        ByteArray TextureEntry
    }

    class Sender {
        UUID ID
        Boolean IsTrial
    }

    class VisualParam {
        Int ParamValue
    }

    AvatarAppearance() {
        this.zeroCoded = true
        this.Sender_Field = Sender()
        this.ObjectData_Field = ObjectData()
    }

    fun CalcPayloadSize(): Int {
        return this.ObjectData_Field.TextureEntry.size + 2 + 21 + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AppearanceData_Fields.size() * 9)
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAvatarAppearance(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -98)
        packUUID(byteBuffer, this.Sender_Field.ID)
        packBoolean(byteBuffer, this.Sender_Field.IsTrial)
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2)
        byteBuffer.put((this as byte).VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (visualParam as byte).ParamValue)
        }
        byteBuffer.put((this as byte).AppearanceData_Fields.size())
        for (AppearanceData appearanceData : this.AppearanceData_Fields) {
            packByte(byteBuffer, (appearanceData as byte).AppearanceVersion)
            packInt(byteBuffer, appearanceData.CofVersion)
            packInt(byteBuffer, appearanceData.Flags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Sender_Field.ID = unpackUUID(byteBuffer)
        this.Sender_Field.IsTrial = unpackBoolean(byteBuffer)
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            VisualParam visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            AppearanceData appearanceData = AppearanceData()
            appearanceData.AppearanceVersion = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            appearanceData.CofVersion = unpackInt(byteBuffer)
            appearanceData.Flags = unpackInt(byteBuffer)
            this.AppearanceData_Fields.add(appearanceData)
        }
    }
}
