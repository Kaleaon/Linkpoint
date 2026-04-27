// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SetCPURatio extends SLMessage
{
    public static class Data
    {

        public int Ratio;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public SetCPURatio()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSetCPURatio(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)71);
        packByte(bytebuffer, (byte)Data_Field.Ratio);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.Ratio = unpackByte(bytebuffer) & 0xff;
    }
}
