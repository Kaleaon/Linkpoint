// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EconomyData extends SLMessage
{
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

        public Info()
        {
        }
    }


    public Info Info_Field;

    public EconomyData()
    {
        zeroCoded = true;
        Info_Field = new Info();
    }

    public int CalcPayloadSize()
    {
        return 72;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEconomyData(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)25);
        packInt(bytebuffer, Info_Field.ObjectCapacity);
        packInt(bytebuffer, Info_Field.ObjectCount);
        packInt(bytebuffer, Info_Field.PriceEnergyUnit);
        packInt(bytebuffer, Info_Field.PriceObjectClaim);
        packInt(bytebuffer, Info_Field.PricePublicObjectDecay);
        packInt(bytebuffer, Info_Field.PricePublicObjectDelete);
        packInt(bytebuffer, Info_Field.PriceParcelClaim);
        packFloat(bytebuffer, Info_Field.PriceParcelClaimFactor);
        packInt(bytebuffer, Info_Field.PriceUpload);
        packInt(bytebuffer, Info_Field.PriceRentLight);
        packInt(bytebuffer, Info_Field.TeleportMinPrice);
        packFloat(bytebuffer, Info_Field.TeleportPriceExponent);
        packFloat(bytebuffer, Info_Field.EnergyEfficiency);
        packFloat(bytebuffer, Info_Field.PriceObjectRent);
        packFloat(bytebuffer, Info_Field.PriceObjectScaleFactor);
        packInt(bytebuffer, Info_Field.PriceParcelRent);
        packInt(bytebuffer, Info_Field.PriceGroupCreate);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Info_Field.ObjectCapacity = unpackInt(bytebuffer);
        Info_Field.ObjectCount = unpackInt(bytebuffer);
        Info_Field.PriceEnergyUnit = unpackInt(bytebuffer);
        Info_Field.PriceObjectClaim = unpackInt(bytebuffer);
        Info_Field.PricePublicObjectDecay = unpackInt(bytebuffer);
        Info_Field.PricePublicObjectDelete = unpackInt(bytebuffer);
        Info_Field.PriceParcelClaim = unpackInt(bytebuffer);
        Info_Field.PriceParcelClaimFactor = unpackFloat(bytebuffer);
        Info_Field.PriceUpload = unpackInt(bytebuffer);
        Info_Field.PriceRentLight = unpackInt(bytebuffer);
        Info_Field.TeleportMinPrice = unpackInt(bytebuffer);
        Info_Field.TeleportPriceExponent = unpackFloat(bytebuffer);
        Info_Field.EnergyEfficiency = unpackFloat(bytebuffer);
        Info_Field.PriceObjectRent = unpackFloat(bytebuffer);
        Info_Field.PriceObjectScaleFactor = unpackFloat(bytebuffer);
        Info_Field.PriceParcelRent = unpackInt(bytebuffer);
        Info_Field.PriceGroupCreate = unpackInt(bytebuffer);
    }
}
