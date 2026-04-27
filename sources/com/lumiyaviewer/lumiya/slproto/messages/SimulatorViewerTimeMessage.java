// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimulatorViewerTimeMessage extends SLMessage
{
    public static class TimeInfo
    {

        public int SecPerDay;
        public int SecPerYear;
        public LLVector3 SunAngVelocity;
        public LLVector3 SunDirection;
        public float SunPhase;
        public long UsecSinceStart;

        public TimeInfo()
        {
        }
    }


    public TimeInfo TimeInfo_Field;

    public SimulatorViewerTimeMessage()
    {
        zeroCoded = false;
        TimeInfo_Field = new TimeInfo();
    }

    public int CalcPayloadSize()
    {
        return 48;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorViewerTimeMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-106);
        packLong(bytebuffer, TimeInfo_Field.UsecSinceStart);
        packInt(bytebuffer, TimeInfo_Field.SecPerDay);
        packInt(bytebuffer, TimeInfo_Field.SecPerYear);
        packLLVector3(bytebuffer, TimeInfo_Field.SunDirection);
        packFloat(bytebuffer, TimeInfo_Field.SunPhase);
        packLLVector3(bytebuffer, TimeInfo_Field.SunAngVelocity);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TimeInfo_Field.UsecSinceStart = unpackLong(bytebuffer);
        TimeInfo_Field.SecPerDay = unpackInt(bytebuffer);
        TimeInfo_Field.SecPerYear = unpackInt(bytebuffer);
        TimeInfo_Field.SunDirection = unpackLLVector3(bytebuffer);
        TimeInfo_Field.SunPhase = unpackFloat(bytebuffer);
        TimeInfo_Field.SunAngVelocity = unpackLLVector3(bytebuffer);
    }
}
