// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LayerData extends SLMessage
{
    public static class LayerDataData
    {

        public byte Data[];

        public LayerDataData()
        {
        }
    }

    public static class LayerID
    {

        public int Type;

        public LayerID()
        {
        }
    }


    public LayerDataData LayerDataData_Field;
    public LayerID LayerID_Field;

    public LayerData()
    {
        zeroCoded = false;
        LayerID_Field = new LayerID();
        LayerDataData_Field = new LayerDataData();
    }

    public int CalcPayloadSize()
    {
        return LayerDataData_Field.Data.length + 2 + 2;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLayerData(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)11);
        packByte(bytebuffer, (byte)LayerID_Field.Type);
        packVariable(bytebuffer, LayerDataData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        LayerID_Field.Type = unpackByte(bytebuffer) & 0xff;
        LayerDataData_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
