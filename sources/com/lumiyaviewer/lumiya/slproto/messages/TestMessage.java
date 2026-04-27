// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TestMessage extends SLMessage
{
    public static class NeighborBlock
    {

        public int Test0;
        public int Test1;
        public int Test2;

        public NeighborBlock()
        {
        }
    }

    public static class TestBlock1
    {

        public int Test1;

        public TestBlock1()
        {
        }
    }


    public NeighborBlock NeighborBlock_Fields[];
    public TestBlock1 TestBlock1_Field;

    public TestMessage()
    {
        NeighborBlock_Fields = new NeighborBlock[4];
        zeroCoded = true;
        TestBlock1_Field = new TestBlock1();
        for (int i = 0; i < 4; i++)
        {
            NeighborBlock_Fields[i] = new NeighborBlock();
        }

    }

    public int CalcPayloadSize()
    {
        return 56;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTestMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        int i = 0;
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)1);
        packInt(bytebuffer, TestBlock1_Field.Test1);
        for (; i < 4; i++)
        {
            packInt(bytebuffer, NeighborBlock_Fields[i].Test0);
            packInt(bytebuffer, NeighborBlock_Fields[i].Test1);
            packInt(bytebuffer, NeighborBlock_Fields[i].Test2);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TestBlock1_Field.Test1 = unpackInt(bytebuffer);
        for (int i = 0; i < 4; i++)
        {
            NeighborBlock_Fields[i].Test0 = unpackInt(bytebuffer);
            NeighborBlock_Fields[i].Test1 = unpackInt(bytebuffer);
            NeighborBlock_Fields[i].Test2 = unpackInt(bytebuffer);
        }

    }
}
