// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class HealthMessage extends SLMessage
{
    public static class HealthData
    {

        public float Health;

        public HealthData()
        {
        }
    }


    public HealthData HealthData_Field;

    public HealthMessage()
    {
        zeroCoded = true;
        HealthData_Field = new HealthData();
    }

    public int CalcPayloadSize()
    {
        return 8;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleHealthMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-118);
        packFloat(bytebuffer, HealthData_Field.Health);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        HealthData_Field.Health = unpackFloat(bytebuffer);
    }
}
