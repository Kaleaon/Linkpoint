package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class EconomyData : SLMessage {
    var Info_Field: Info = Info()

    class Info {
        var ObjectCapacity: Int = 0
        var ObjectCount: Int = 0
        var PriceEnergyUnit: Int = 0
        var PriceGroupCreate: Int = 0
        var PriceObjectClaim: Int = 0
        var PriceObjectRent: Float = 0f
        var PriceObjectScaleFactor: Float = 0f
        var PriceParcelClaim: Int = 0
        var PriceParcelClaimFactor: Float = 0f
        var PriceParcelRent: Int = 0
        var PricePublicObjectDecay: Int = 0
        var PricePublicObjectDelete: Int = 0
        var PriceRentLight: Int = 0
        var PriceUpload: Int = 0
        var TeleportMinPrice: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 56
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEconomyData(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(36.toByte())
        packInt(buffer, this.Info_Field.ObjectCapacity)
        packInt(buffer, this.Info_Field.ObjectCount)
        packInt(buffer, this.Info_Field.PriceEnergyUnit)
        packInt(buffer, this.Info_Field.PriceObjectClaim)
        packInt(buffer, this.Info_Field.PricePublicObjectDecay)
        packInt(buffer, this.Info_Field.PricePublicObjectDelete)
        packInt(buffer, this.Info_Field.PriceParcelClaim)
        packFloat(buffer, this.Info_Field.PriceParcelClaimFactor)
        packInt(buffer, this.Info_Field.PriceUpload)
        packInt(buffer, this.Info_Field.PriceRentLight)
        packInt(buffer, this.Info_Field.PriceObjectRent) // Wait, primitive type mismatch in Java vs Kotlin inference? It's int in SL protocol usually, but let's check standard
        // Actually standard LSL protocol for EconomyData:
        // ObjectCapacity S32
        // ObjectCount S32
        // PriceEnergyUnit S32
        // PriceObjectClaim S32
        // PricePublicObjectDecay S32
        // PricePublicObjectDelete S32
        // PriceParcelClaim S32
        // PriceParcelClaimFactor F32
        // PriceUpload S32
        // PriceRentLight S32
        // PriceObjectRent F32
        // PriceObjectScaleFactor F32
        // PriceParcelRent S32
        // PriceGroupCreate S32
        // TeleportMinPrice S32
        
        packFloat(buffer, this.Info_Field.PriceObjectRent)
        packFloat(buffer, this.Info_Field.PriceObjectScaleFactor)
        packInt(buffer, this.Info_Field.PriceParcelRent)
        packInt(buffer, this.Info_Field.PriceGroupCreate)
        packInt(buffer, this.Info_Field.TeleportMinPrice)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.Info_Field.ObjectCapacity = unpackInt(buffer)
        this.Info_Field.ObjectCount = unpackInt(buffer)
        this.Info_Field.PriceEnergyUnit = unpackInt(buffer)
        this.Info_Field.PriceObjectClaim = unpackInt(buffer)
        this.Info_Field.PricePublicObjectDecay = unpackInt(buffer)
        this.Info_Field.PricePublicObjectDelete = unpackInt(buffer)
        this.Info_Field.PriceParcelClaim = unpackInt(buffer)
        this.Info_Field.PriceParcelClaimFactor = unpackFloat(buffer)
        this.Info_Field.PriceUpload = unpackInt(buffer)
        this.Info_Field.PriceRentLight = unpackInt(buffer)
        this.Info_Field.PriceObjectRent = unpackFloat(buffer)
        this.Info_Field.PriceObjectScaleFactor = unpackFloat(buffer)
        this.Info_Field.PriceParcelRent = unpackInt(buffer)
        this.Info_Field.PriceGroupCreate = unpackInt(buffer)
        this.Info_Field.TeleportMinPrice = unpackInt(buffer)
    }
}
