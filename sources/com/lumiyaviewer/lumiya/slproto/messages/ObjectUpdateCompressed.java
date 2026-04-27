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

public class ObjectUpdateCompressed extends SLMessage
{
    public static class ObjectData
    {

        public byte Data[];
        public int UpdateFlags;

        public ObjectData()
        {
        }
    }

    public static class RegionData
    {

        public long RegionHandle;
        public int TimeDilation;

        public RegionData()
        {
        }
    }


    public ArrayList ObjectData_Fields;
    public RegionData RegionData_Field;

    public ObjectUpdateCompressed()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = false;
        RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        int i;
        for (i = 12; iterator.hasNext(); i = ((ObjectData)iterator.next()).Data.length + 6 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectUpdateCompressed(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)13);
        packLong(bytebuffer, RegionData_Field.RegionHandle);
        packShort(bytebuffer, (short)RegionData_Field.TimeDilation);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.Data, 2))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.UpdateFlags);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RegionData_Field.RegionHandle = unpackLong(bytebuffer);
        RegionData_Field.TimeDilation = unpackShort(bytebuffer) & 0xffff;
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.UpdateFlags = unpackInt(bytebuffer);
            objectdata.Data = unpackVariable(bytebuffer, 2);
            ObjectData_Fields.add(objectdata);
        }

    }
}
