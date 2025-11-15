package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] TextureEntry
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

    Int CalcPayloadSize() {
        return this.ObjectData_Field.TextureEntry.length + 2 + 21 + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AppearanceData_Fields.size() * 9)
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarAppearance(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -98)
        packUUID(byteBuffer, this.Sender_Field.ID)
        packBoolean(byteBuffer, this.Sender_Field.IsTrial)
        packVariable(byteBuffer, this.ObjectData_Field.TextureEntry, 2)
        byteBuffer.put((byte) this.VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (byte) visualParam.ParamValue)
        }
        byteBuffer.put((byte) this.AppearanceData_Fields.size())
        for (AppearanceData appearanceData : this.AppearanceData_Fields) {
            packByte(byteBuffer, (byte) appearanceData.AppearanceVersion)
            packInt(byteBuffer, appearanceData.CofVersion)
            packInt(byteBuffer, appearanceData.Flags)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Sender_Field.ID = unpackUUID(byteBuffer)
        this.Sender_Field.IsTrial = unpackBoolean(byteBuffer)
        this.ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            VisualParam visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            AppearanceData appearanceData = AppearanceData()
            appearanceData.AppearanceVersion = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            appearanceData.CofVersion = unpackInt(byteBuffer)
            appearanceData.Flags = unpackInt(byteBuffer)
            this.AppearanceData_Fields.add(appearanceData)
        }
    }
}
