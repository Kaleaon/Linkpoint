// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ViewerFrozenMessage extends SLMessage
{
    public static class FrozenData
    {

        public boolean Data;

        public FrozenData()
        {
        }
    }


    public FrozenData FrozenData_Field;

    public ViewerFrozenMessage()
    {
        zeroCoded = false;
        FrozenData_Field = new FrozenData();
    }

    public int CalcPayloadSize()
    {
        return 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleViewerFrozenMessage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-119);
        packBoolean(bytebuffer, FrozenData_Field.Data);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        FrozenData_Field.Data = unpackBoolean(bytebuffer);
    }
}
