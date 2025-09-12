// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelOverlay extends SLMessage
{
    public static class ParcelData
    {

        public byte Data[];
        public int SequenceID;

        public ParcelData()
        {
        }
    }


    public ParcelData ParcelData_Field;

    public ParcelOverlay()
    {
        zeroCoded = true;
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Field.Data.length + 6 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelOverlay(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-60);
        packInt(bytebuffer, ParcelData_Field.SequenceID);
        packVariable(bytebuffer, ParcelData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ParcelData_Field.SequenceID = unpackInt(bytebuffer);
        ParcelData_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
