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

public class RemoveNameValuePair extends SLMessage
{
    public static class NameValueData
    {

        public byte NVPair[];

        public NameValueData()
        {
        }
    }

    public static class TaskData
    {

        public UUID ID;

        public TaskData()
        {
        }
    }


    public ArrayList NameValueData_Fields;
    public TaskData TaskData_Field;

    public RemoveNameValuePair()
    {
        NameValueData_Fields = new ArrayList();
        zeroCoded = false;
        TaskData_Field = new TaskData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = NameValueData_Fields.iterator();
        int i;
        for (i = 21; iterator.hasNext(); i = ((NameValueData)iterator.next()).NVPair.length + 2 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRemoveNameValuePair(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)74);
        packUUID(bytebuffer, TaskData_Field.ID);
        bytebuffer.put((byte)NameValueData_Fields.size());
        for (Iterator iterator = NameValueData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, ((NameValueData)iterator.next()).NVPair, 2)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TaskData_Field.ID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            NameValueData namevaluedata = new NameValueData();
            namevaluedata.NVPair = unpackVariable(bytebuffer, 2);
            NameValueData_Fields.add(namevaluedata);
        }

    }
}
