package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class EconomyData : SLMessage() {
    public Info Info_Field = Info()

    @JvmStatic
    class Info {
        public Float EnergyEfficiency
        public Int ObjectCapacity
        public Int ObjectCount
        public Int PriceEnergyUnit
        public Int PriceGroupCreate
        public Int PriceObjectClaim
        public Float PriceObjectRent
        public Float PriceObjectScaleFactor
        public Int PriceParcelClaim
        public Float PriceParcelClaimFactor
        public Int PriceParcelRent
        public Int PricePublicObjectDecay
        public Int PricePublicObjectDelete
        public Int PriceRentLight
        public Int PriceUpload
        public Int TeleportMinPrice
        public Float TeleportPriceExponent
    }

    public EconomyData() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 72
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEconomyData(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put(Ascii.EM)
        packInt(byteBuffer, this.Info_Field.ObjectCapacity)
        packInt(byteBuffer, this.Info_Field.ObjectCount)
        packInt(byteBuffer, this.Info_Field.PriceEnergyUnit)
        packInt(byteBuffer, this.Info_Field.PriceObjectClaim)
        packInt(byteBuffer, this.Info_Field.PricePublicObjectDecay)
        packInt(byteBuffer, this.Info_Field.PricePublicObjectDelete)
        packInt(byteBuffer, this.Info_Field.PriceParcelClaim)
        packFloat(byteBuffer, this.Info_Field.PriceParcelClaimFactor)
        packInt(byteBuffer, this.Info_Field.PriceUpload)
        packInt(byteBuffer, this.Info_Field.PriceRentLight)
        packInt(byteBuffer, this.Info_Field.TeleportMinPrice)
        packFloat(byteBuffer, this.Info_Field.TeleportPriceExponent)
        packFloat(byteBuffer, this.Info_Field.EnergyEfficiency)
        packFloat(byteBuffer, this.Info_Field.PriceObjectRent)
        packFloat(byteBuffer, this.Info_Field.PriceObjectScaleFactor)
        packInt(byteBuffer, this.Info_Field.PriceParcelRent)
        packInt(byteBuffer, this.Info_Field.PriceGroupCreate)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.ObjectCapacity = unpackInt(byteBuffer)
        this.Info_Field.ObjectCount = unpackInt(byteBuffer)
        this.Info_Field.PriceEnergyUnit = unpackInt(byteBuffer)
        this.Info_Field.PriceObjectClaim = unpackInt(byteBuffer)
        this.Info_Field.PricePublicObjectDecay = unpackInt(byteBuffer)
        this.Info_Field.PricePublicObjectDelete = unpackInt(byteBuffer)
        this.Info_Field.PriceParcelClaim = unpackInt(byteBuffer)
        this.Info_Field.PriceParcelClaimFactor = unpackFloat(byteBuffer)
        this.Info_Field.PriceUpload = unpackInt(byteBuffer)
        this.Info_Field.PriceRentLight = unpackInt(byteBuffer)
        this.Info_Field.TeleportMinPrice = unpackInt(byteBuffer)
        this.Info_Field.TeleportPriceExponent = unpackFloat(byteBuffer)
        this.Info_Field.EnergyEfficiency = unpackFloat(byteBuffer)
        this.Info_Field.PriceObjectRent = unpackFloat(byteBuffer)
        this.Info_Field.PriceObjectScaleFactor = unpackFloat(byteBuffer)
        this.Info_Field.PriceParcelRent = unpackInt(byteBuffer)
        this.Info_Field.PriceGroupCreate = unpackInt(byteBuffer)
    }
}
