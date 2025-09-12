// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ConfirmAuctionStart extends SLMessage
{
    public static class AuctionData
    {

        public int AuctionID;
        public UUID ParcelID;

        public AuctionData()
        {
        }
    }


    public AuctionData AuctionData_Field;

    public ConfirmAuctionStart()
    {
        zeroCoded = false;
        AuctionData_Field = new AuctionData();
    }

    public int CalcPayloadSize()
    {
        return 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleConfirmAuctionStart(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-26);
        packUUID(bytebuffer, AuctionData_Field.ParcelID);
        packInt(bytebuffer, AuctionData_Field.AuctionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AuctionData_Field.ParcelID = unpackUUID(bytebuffer);
        AuctionData_Field.AuctionID = unpackInt(bytebuffer);
    }
}
