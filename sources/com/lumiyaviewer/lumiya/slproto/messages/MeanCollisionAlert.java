// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MeanCollisionAlert extends SLMessage
{
    public static class MeanCollision
    {

        public float Mag;
        public UUID Perp;
        public int Time;
        public int Type;
        public UUID Victim;

        public MeanCollision()
        {
        }
    }


    public ArrayList MeanCollision_Fields;

    public MeanCollisionAlert()
    {
        MeanCollision_Fields = new ArrayList();
        zeroCoded = true;
    }

    public int CalcPayloadSize()
    {
        return MeanCollision_Fields.size() * 41 + 5;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMeanCollisionAlert(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-120);
        bytebuffer.put((byte)MeanCollision_Fields.size());
        MeanCollision meancollision;
        for (Iterator iterator = MeanCollision_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)meancollision.Type))
        {
            meancollision = (MeanCollision)iterator.next();
            packUUID(bytebuffer, meancollision.Victim);
            packUUID(bytebuffer, meancollision.Perp);
            packInt(bytebuffer, meancollision.Time);
            packFloat(bytebuffer, meancollision.Mag);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            MeanCollision meancollision = new MeanCollision();
            meancollision.Victim = unpackUUID(bytebuffer);
            meancollision.Perp = unpackUUID(bytebuffer);
            meancollision.Time = unpackInt(bytebuffer);
            meancollision.Mag = unpackFloat(bytebuffer);
            meancollision.Type = unpackByte(bytebuffer) & 0xff;
            MeanCollision_Fields.add(meancollision);
        }

    }
}
