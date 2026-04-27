// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class KillChildAgents extends SLMessage
{
    public static class IDBlock
    {

        public UUID AgentID;

        public IDBlock()
        {
        }
    }


    public IDBlock IDBlock_Field;

    public KillChildAgents()
    {
        zeroCoded = false;
        IDBlock_Field = new IDBlock();
    }

    public int CalcPayloadSize()
    {
        return 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleKillChildAgents(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-14);
        packUUID(bytebuffer, IDBlock_Field.AgentID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        IDBlock_Field.AgentID = unpackUUID(bytebuffer);
    }
}
