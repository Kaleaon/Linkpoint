// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AtomicPassObject extends SLMessage
{
    public static class TaskData
    {

        public boolean AttachmentNeedsSave;
        public UUID TaskID;

        public TaskData()
        {
        }
    }


    public TaskData TaskData_Field;

    public AtomicPassObject()
    {
        zeroCoded = false;
        TaskData_Field = new TaskData();
    }

    public int CalcPayloadSize()
    {
        return 18;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAtomicPassObject(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)28);
        packUUID(bytebuffer, TaskData_Field.TaskID);
        packBoolean(bytebuffer, TaskData_Field.AttachmentNeedsSave);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TaskData_Field.TaskID = unpackUUID(bytebuffer);
        TaskData_Field.AttachmentNeedsSave = unpackBoolean(bytebuffer);
    }
}
