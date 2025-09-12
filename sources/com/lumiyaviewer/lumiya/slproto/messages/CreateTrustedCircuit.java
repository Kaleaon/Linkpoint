// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CreateTrustedCircuit extends SLMessage
{
    public static class DataBlock
    {

        public byte Digest[];
        public UUID EndPointID;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public CreateTrustedCircuit()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return 52;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCreateTrustedCircuit(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-120);
        packUUID(bytebuffer, DataBlock_Field.EndPointID);
        packFixed(bytebuffer, DataBlock_Field.Digest, 32);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.EndPointID = unpackUUID(bytebuffer);
        DataBlock_Field.Digest = unpackFixed(bytebuffer, 32);
    }
}
