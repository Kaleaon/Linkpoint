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

public class ParcelObjectOwnersReply extends SLMessage
{
    public static class Data
    {

        public int Count;
        public boolean IsGroupOwned;
        public boolean OnlineStatus;
        public UUID OwnerID;

        public Data()
        {
        }
    }


    public ArrayList Data_Fields;

    public ParcelObjectOwnersReply()
    {
        Data_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        return Data_Fields.size() * 22 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelObjectOwnersReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)57);
        bytebuffer.put((byte)Data_Fields.size());
        Data data;
        for (Iterator iterator = Data_Fields.iterator(); iterator.hasNext(); packBoolean(bytebuffer, data.OnlineStatus))
        {
            data = (Data)iterator.next();
            packUUID(bytebuffer, data.OwnerID);
            packBoolean(bytebuffer, data.IsGroupOwned);
            packInt(bytebuffer, data.Count);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Data data = new Data();
            data.OwnerID = unpackUUID(bytebuffer);
            data.IsGroupOwned = unpackBoolean(bytebuffer);
            data.Count = unpackInt(bytebuffer);
            data.OnlineStatus = unpackBoolean(bytebuffer);
            Data_Fields.add(data);
        }

    }
}
