// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class KillObject extends SLMessage
{
    public static class ObjectData
    {

        public int ID;

        public ObjectData()
        {
        }
    }


    public ArrayList ObjectData_Fields;

    public KillObject()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 4 + 2;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleKillObject(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)16);
        bytebuffer.put((byte)ObjectData_Fields.size());
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((ObjectData)iterator.next()).ID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ID = unpackInt(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
