package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class EconomyData extends SLMessage {
    public Info Info_Field = new Info();

    public static class Info {
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

    public EconomyData() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 72;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEconomyData(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.EM);
        packInt(byteBuffer, this.Info_Field.ObjectCapacity);
        packInt(byteBuffer, this.Info_Field.ObjectCount);
        packInt(byteBuffer, this.Info_Field.PriceEnergyUnit);
        packInt(byteBuffer, this.Info_Field.PriceObjectClaim);
        packInt(byteBuffer, this.Info_Field.PricePublicObjectDecay);
        packInt(byteBuffer, this.Info_Field.PricePublicObjectDelete);
        packInt(byteBuffer, this.Info_Field.PriceParcelClaim);
        packFloat(byteBuffer, this.Info_Field.PriceParcelClaimFactor);
        packInt(byteBuffer, this.Info_Field.PriceUpload);
        packInt(byteBuffer, this.Info_Field.PriceRentLight);
        packInt(byteBuffer, this.Info_Field.TeleportMinPrice);
        packFloat(byteBuffer, this.Info_Field.TeleportPriceExponent);
        packFloat(byteBuffer, this.Info_Field.EnergyEfficiency);
        packFloat(byteBuffer, this.Info_Field.PriceObjectRent);
        packFloat(byteBuffer, this.Info_Field.PriceObjectScaleFactor);
        packInt(byteBuffer, this.Info_Field.PriceParcelRent);
        packInt(byteBuffer, this.Info_Field.PriceGroupCreate);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.ObjectCapacity = unpackInt(byteBuffer);
        this.Info_Field.ObjectCount = unpackInt(byteBuffer);
        this.Info_Field.PriceEnergyUnit = unpackInt(byteBuffer);
        this.Info_Field.PriceObjectClaim = unpackInt(byteBuffer);
        this.Info_Field.PricePublicObjectDecay = unpackInt(byteBuffer);
        this.Info_Field.PricePublicObjectDelete = unpackInt(byteBuffer);
        this.Info_Field.PriceParcelClaim = unpackInt(byteBuffer);
        this.Info_Field.PriceParcelClaimFactor = unpackFloat(byteBuffer);
        this.Info_Field.PriceUpload = unpackInt(byteBuffer);
        this.Info_Field.PriceRentLight = unpackInt(byteBuffer);
        this.Info_Field.TeleportMinPrice = unpackInt(byteBuffer);
        this.Info_Field.TeleportPriceExponent = unpackFloat(byteBuffer);
        this.Info_Field.EnergyEfficiency = unpackFloat(byteBuffer);
        this.Info_Field.PriceObjectRent = unpackFloat(byteBuffer);
        this.Info_Field.PriceObjectScaleFactor = unpackFloat(byteBuffer);
        this.Info_Field.PriceParcelRent = unpackInt(byteBuffer);
        this.Info_Field.PriceGroupCreate = unpackInt(byteBuffer);
    }
}
