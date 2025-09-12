// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimulatorMapUpdate extends SLMessage
{
    public static class MapData
    {

        public int Flags;

        public MapData()
        {
        }
    }


    public MapData MapData_Field;

    public SimulatorMapUpdate()
    {
        zeroCoded = false;
        MapData_Field = new MapData();
    }

    public int CalcPayloadSize()
    {
        return 8;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorMapUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)5);
        packInt(bytebuffer, MapData_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MapData_Field.Flags = unpackInt(bytebuffer);
    }
}
