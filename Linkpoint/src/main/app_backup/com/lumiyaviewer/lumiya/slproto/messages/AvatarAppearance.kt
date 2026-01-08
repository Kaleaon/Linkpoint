package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarAppearance : SLMessage() {
    var AppearanceData_Fields: ArrayList<AppearanceData> = ArrayList()
    var ObjectData_Field: ObjectData = ObjectData()
    var Sender_Field: Sender = Sender()
    var VisualParam_Fields: ArrayList<VisualParam> = ArrayList()

    class AppearanceData {
        var AppearanceVersion: Int = 0
        var CofVersion: Int = 0
        var Flags: Int = 0
    }

    class ObjectData {
        var TextureEntry: ByteArray = ByteArray(0)
    }

    class Sender {
        var ID: UUID = UUID.randomUUID()
        var IsTrial: Boolean = false
    }

    class VisualParam {
        var ParamValue: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return ObjectData_Field.TextureEntry.size + 2 + 21 + 1 + (VisualParam_Fields.size * 1) + 1 + (AppearanceData_Fields.size * 9)
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarAppearance(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-98).toByte())
        packUUID(byteBuffer, Sender_Field.ID)
        packBoolean(byteBuffer, Sender_Field.IsTrial)
        packVariable(byteBuffer, ObjectData_Field.TextureEntry, 2)
        byteBuffer.put(VisualParam_Fields.size.toByte())
        for (visualParam in VisualParam_Fields) {
            packByte(byteBuffer, visualParam.ParamValue.toByte())
        }
        byteBuffer.put(AppearanceData_Fields.size.toByte())
        for (appearanceData in AppearanceData_Fields) {
            packByte(byteBuffer, appearanceData.AppearanceVersion.toByte())
            packInt(byteBuffer, appearanceData.CofVersion)
            packInt(byteBuffer, appearanceData.Flags)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        Sender_Field.ID = unpackUUID(byteBuffer)
        Sender_Field.IsTrial = unpackBoolean(byteBuffer)
        ObjectData_Field.TextureEntry = unpackVariable(byteBuffer, 2)
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer).toInt() and 0xFF
            VisualParam_Fields.add(visualParam)
        }
        val b2 = byteBuffer.get().toInt() and 0xFF
        for (i2 in 0 until b2) {
            val appearanceData = AppearanceData()
            appearanceData.AppearanceVersion = unpackByte(byteBuffer).toInt() and 0xFF
            appearanceData.CofVersion = unpackInt(byteBuffer)
            appearanceData.Flags = unpackInt(byteBuffer)
            AppearanceData_Fields.add(appearanceData)
        }
    }
}
