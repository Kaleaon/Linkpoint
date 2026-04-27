// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ReportAutosaveCrash extends SLMessage
{
    public static class AutosaveData
    {

        public int PID;
        public int Status;

        public AutosaveData()
        {
        }
    }


    public AutosaveData AutosaveData_Field;

    public ReportAutosaveCrash()
    {
        zeroCoded = false;
        AutosaveData_Field = new AutosaveData();
    }

    public int CalcPayloadSize()
    {
        return 12;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleReportAutosaveCrash(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-128);
        packInt(bytebuffer, AutosaveData_Field.PID);
        packInt(bytebuffer, AutosaveData_Field.Status);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AutosaveData_Field.PID = unpackInt(bytebuffer);
        AutosaveData_Field.Status = unpackInt(bytebuffer);
    }
}
