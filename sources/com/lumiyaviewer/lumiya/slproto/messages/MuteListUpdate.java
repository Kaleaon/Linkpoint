// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MuteListUpdate extends SLMessage
{
    public static class MuteData
    {

        public UUID AgentID;
        public byte Filename[];

        public MuteData()
        {
        }
    }


    public MuteData MuteData_Field;

    public MuteListUpdate()
    {
        zeroCoded = false;
        MuteData_Field = new MuteData();
    }

    public int CalcPayloadSize()
    {
        return MuteData_Field.Filename.length + 17 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMuteListUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)62);
        packUUID(bytebuffer, MuteData_Field.AgentID);
        packVariable(bytebuffer, MuteData_Field.Filename, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MuteData_Field.AgentID = unpackUUID(bytebuffer);
        MuteData_Field.Filename = unpackVariable(bytebuffer, 1);
    }
}
