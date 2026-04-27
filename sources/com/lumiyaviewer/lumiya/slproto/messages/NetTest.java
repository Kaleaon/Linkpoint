// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class NetTest extends SLMessage
{
    public static class NetBlock
    {

        public int Port;

        public NetBlock()
        {
        }
    }


    public NetBlock NetBlock_Field;

    public NetTest()
    {
        zeroCoded = false;
        NetBlock_Field = new NetBlock();
    }

    public int CalcPayloadSize()
    {
        return 6;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleNetTest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)70);
        packShort(bytebuffer, (short)NetBlock_Field.Port);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        NetBlock_Field.Port = unpackShort(bytebuffer) & 0xffff;
    }
}
