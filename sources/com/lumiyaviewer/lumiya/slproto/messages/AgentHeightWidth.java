// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AgentHeightWidth extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class HeightWidthBlock
    {

        public int GenCounter;
        public int Height;
        public int Width;

        public HeightWidthBlock()
        {
        }
    }


    public AgentData AgentData_Field;
    public HeightWidthBlock HeightWidthBlock_Field;

    public AgentHeightWidth()
    {
        zeroCoded = false;
        AgentData_Field = new AgentData();
        HeightWidthBlock_Field = new HeightWidthBlock();
    }

    public int CalcPayloadSize()
    {
        return 48;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAgentHeightWidth(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)83);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.CircuitCode);
        packInt(bytebuffer, HeightWidthBlock_Field.GenCounter);
        packShort(bytebuffer, (short)HeightWidthBlock_Field.Height);
        packShort(bytebuffer, (short)HeightWidthBlock_Field.Width);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.CircuitCode = unpackInt(bytebuffer);
        HeightWidthBlock_Field.GenCounter = unpackInt(bytebuffer);
        HeightWidthBlock_Field.Height = unpackShort(bytebuffer) & 0xffff;
        HeightWidthBlock_Field.Width = unpackShort(bytebuffer) & 0xffff;
    }
}
