// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimStatus extends SLMessage
{
    public static class SimStatusData
    {

        public boolean CanAcceptAgents;
        public boolean CanAcceptTasks;

        public SimStatusData()
        {
        }
    }


    public SimStatusData SimStatusData_Field;

    public SimStatus()
    {
        zeroCoded = false;
        SimStatusData_Field = new SimStatusData();
    }

    public int CalcPayloadSize()
    {
        return 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimStatus(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)12);
        packBoolean(bytebuffer, SimStatusData_Field.CanAcceptAgents);
        packBoolean(bytebuffer, SimStatusData_Field.CanAcceptTasks);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SimStatusData_Field.CanAcceptAgents = unpackBoolean(bytebuffer);
        SimStatusData_Field.CanAcceptTasks = unpackBoolean(bytebuffer);
    }
}
