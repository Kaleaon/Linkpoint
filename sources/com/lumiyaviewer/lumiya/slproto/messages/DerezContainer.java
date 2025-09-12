// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DerezContainer extends SLMessage
{
    public static class Data
    {

        public boolean Delete;
        public UUID ObjectID;

        public Data()
        {
        }
    }


    public Data Data_Field;

    public DerezContainer()
    {
        zeroCoded = true;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return 21;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDerezContainer(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)104);
        packUUID(bytebuffer, Data_Field.ObjectID);
        packBoolean(bytebuffer, Data_Field.Delete);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.ObjectID = unpackUUID(bytebuffer);
        Data_Field.Delete = unpackBoolean(bytebuffer);
    }
}
