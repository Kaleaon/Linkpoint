// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LogDwellTime extends SLMessage
{
    public static class DwellInfo
    {

        public UUID AgentID;
        public int AvgAgentsInView;
        public int AvgViewerFPS;
        public float Duration;
        public int RegionX;
        public int RegionY;
        public UUID SessionID;
        public byte SimName[];

        public DwellInfo()
        {
        }
    }


    public DwellInfo DwellInfo_Field;

    public LogDwellTime()
    {
        zeroCoded = false;
        DwellInfo_Field = new DwellInfo();
    }

    public int CalcPayloadSize()
    {
        return DwellInfo_Field.SimName.length + 37 + 4 + 4 + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLogDwellTime(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)18);
        packUUID(bytebuffer, DwellInfo_Field.AgentID);
        packUUID(bytebuffer, DwellInfo_Field.SessionID);
        packFloat(bytebuffer, DwellInfo_Field.Duration);
        packVariable(bytebuffer, DwellInfo_Field.SimName, 1);
        packInt(bytebuffer, DwellInfo_Field.RegionX);
        packInt(bytebuffer, DwellInfo_Field.RegionY);
        packByte(bytebuffer, (byte)DwellInfo_Field.AvgAgentsInView);
        packByte(bytebuffer, (byte)DwellInfo_Field.AvgViewerFPS);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DwellInfo_Field.AgentID = unpackUUID(bytebuffer);
        DwellInfo_Field.SessionID = unpackUUID(bytebuffer);
        DwellInfo_Field.Duration = unpackFloat(bytebuffer);
        DwellInfo_Field.SimName = unpackVariable(bytebuffer, 1);
        DwellInfo_Field.RegionX = unpackInt(bytebuffer);
        DwellInfo_Field.RegionY = unpackInt(bytebuffer);
        DwellInfo_Field.AvgAgentsInView = unpackByte(bytebuffer) & 0xff;
        DwellInfo_Field.AvgViewerFPS = unpackByte(bytebuffer) & 0xff;
    }
}
