// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EdgeDataPacket extends SLMessage
{
    public static class EdgeData
    {

        public int Direction;
        public byte LayerData[];
        public int LayerType;

        public EdgeData()
        {
        }
    }


    public EdgeData EdgeData_Field;

    public EdgeDataPacket()
    {
        zeroCoded = true;
        EdgeData_Field = new EdgeData();
    }

    public int CalcPayloadSize()
    {
        return EdgeData_Field.LayerData.length + 4 + 1;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEdgeDataPacket(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)24);
        packByte(bytebuffer, (byte)EdgeData_Field.LayerType);
        packByte(bytebuffer, (byte)EdgeData_Field.Direction);
        packVariable(bytebuffer, EdgeData_Field.LayerData, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        EdgeData_Field.LayerType = unpackByte(bytebuffer) & 0xff;
        EdgeData_Field.Direction = unpackByte(bytebuffer) & 0xff;
        EdgeData_Field.LayerData = unpackVariable(bytebuffer, 2);
    }
}
