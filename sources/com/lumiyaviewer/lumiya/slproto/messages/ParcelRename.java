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

public class ParcelRename extends SLMessage
{
    public static class ParcelData
    {

        public byte NewName[];
        public UUID ParcelID;

        public ParcelData()
        {
        }
    }


    public ArrayList ParcelData_Fields;

    public ParcelRename()
    {
        ParcelData_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ParcelData_Fields.iterator();
        int i;
        for (i = 5; iterator.hasNext(); i = ((ParcelData)iterator.next()).NewName.length + 17 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelRename(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-110);
        bytebuffer.put((byte)ParcelData_Fields.size());
        ParcelData parceldata;
        for (Iterator iterator = ParcelData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, parceldata.NewName, 1))
        {
            parceldata = (ParcelData)iterator.next();
            packUUID(bytebuffer, parceldata.ParcelID);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParcelData parceldata = new ParcelData();
            parceldata.ParcelID = unpackUUID(bytebuffer);
            parceldata.NewName = unpackVariable(bytebuffer, 1);
            ParcelData_Fields.add(parceldata);
        }

    }
}
