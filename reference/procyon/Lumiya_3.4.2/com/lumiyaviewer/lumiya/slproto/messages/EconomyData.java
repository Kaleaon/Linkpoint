// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EconomyData extends SLMessage
{
    public Info Info_Field;
    
    public EconomyData() {
        this.zeroCoded = true;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEconomyData(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)25);
        this.packInt(byteBuffer, this.Info_Field.ObjectCapacity);
        this.packInt(byteBuffer, this.Info_Field.ObjectCount);
        this.packInt(byteBuffer, this.Info_Field.PriceEnergyUnit);
        this.packInt(byteBuffer, this.Info_Field.PriceObjectClaim);
        this.packInt(byteBuffer, this.Info_Field.PricePublicObjectDecay);
        this.packInt(byteBuffer, this.Info_Field.PricePublicObjectDelete);
        this.packInt(byteBuffer, this.Info_Field.PriceParcelClaim);
        this.packFloat(byteBuffer, this.Info_Field.PriceParcelClaimFactor);
        this.packInt(byteBuffer, this.Info_Field.PriceUpload);
        this.packInt(byteBuffer, this.Info_Field.PriceRentLight);
        this.packInt(byteBuffer, this.Info_Field.TeleportMinPrice);
        this.packFloat(byteBuffer, this.Info_Field.TeleportPriceExponent);
        this.packFloat(byteBuffer, this.Info_Field.EnergyEfficiency);
        this.packFloat(byteBuffer, this.Info_Field.PriceObjectRent);
        this.packFloat(byteBuffer, this.Info_Field.PriceObjectScaleFactor);
        this.packInt(byteBuffer, this.Info_Field.PriceParcelRent);
        this.packInt(byteBuffer, this.Info_Field.PriceGroupCreate);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.ObjectCapacity = this.unpackInt(byteBuffer);
        this.Info_Field.ObjectCount = this.unpackInt(byteBuffer);
        this.Info_Field.PriceEnergyUnit = this.unpackInt(byteBuffer);
        this.Info_Field.PriceObjectClaim = this.unpackInt(byteBuffer);
        this.Info_Field.PricePublicObjectDecay = this.unpackInt(byteBuffer);
        this.Info_Field.PricePublicObjectDelete = this.unpackInt(byteBuffer);
        this.Info_Field.PriceParcelClaim = this.unpackInt(byteBuffer);
        this.Info_Field.PriceParcelClaimFactor = this.unpackFloat(byteBuffer);
        this.Info_Field.PriceUpload = this.unpackInt(byteBuffer);
        this.Info_Field.PriceRentLight = this.unpackInt(byteBuffer);
        this.Info_Field.TeleportMinPrice = this.unpackInt(byteBuffer);
        this.Info_Field.TeleportPriceExponent = this.unpackFloat(byteBuffer);
        this.Info_Field.EnergyEfficiency = this.unpackFloat(byteBuffer);
        this.Info_Field.PriceObjectRent = this.unpackFloat(byteBuffer);
        this.Info_Field.PriceObjectScaleFactor = this.unpackFloat(byteBuffer);
        this.Info_Field.PriceParcelRent = this.unpackInt(byteBuffer);
        this.Info_Field.PriceGroupCreate = this.unpackInt(byteBuffer);
    }
    
    public static class Info
    {
        public float EnergyEfficiency;
        public int ObjectCapacity;
        public int ObjectCount;
        public int PriceEnergyUnit;
        public int PriceGroupCreate;
        public int PriceObjectClaim;
        public float PriceObjectRent;
        public float PriceObjectScaleFactor;
        public int PriceParcelClaim;
        public float PriceParcelClaimFactor;
        public int PriceParcelRent;
        public int PricePublicObjectDecay;
        public int PricePublicObjectDelete;
        public int PriceRentLight;
        public int PriceUpload;
        public int TeleportMinPrice;
        public float TeleportPriceExponent;
    }
}
