// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimulatorSetMap extends SLMessage
{
    public static class MapData
    {

        public UUID MapImage;
        public long RegionHandle;
        public int Type;

        public MapData()
        {
        }
    }


    public MapData MapData_Field;

    public SimulatorSetMap()
    {
        zeroCoded = false;
        MapData_Field = new MapData();
    }

    public int CalcPayloadSize()
    {
        return 32;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimulatorSetMap(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)6);
        packLong(bytebuffer, MapData_Field.RegionHandle);
        packInt(bytebuffer, MapData_Field.Type);
        packUUID(bytebuffer, MapData_Field.MapImage);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MapData_Field.RegionHandle = unpackLong(bytebuffer);
        MapData_Field.Type = unpackInt(bytebuffer);
        MapData_Field.MapImage = unpackUUID(bytebuffer);
    }
}
