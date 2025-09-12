// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RemoveParcel extends SLMessage
{
    public static class ParcelData
    {

        public UUID ParcelID;

        public ParcelData()
        {
        }
    }


    public ArrayList ParcelData_Fields;

    public RemoveParcel()
    {
        ParcelData_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Fields.size() * 16 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRemoveParcel(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-34);
        bytebuffer.put((byte)ParcelData_Fields.size());
        for (Iterator iterator = ParcelData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, ((ParcelData)iterator.next()).ParcelID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParcelData parceldata = new ParcelData();
            parceldata.ParcelID = unpackUUID(bytebuffer);
            ParcelData_Fields.add(parceldata);
        }

    }
}
