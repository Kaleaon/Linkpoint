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

public class ViewerEffect extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Effect
    {

        public UUID AgentID;
        public byte Color[];
        public float Duration;
        public UUID ID;
        public int Type;
        public byte TypeData[];

        public Effect()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList Effect_Fields;

    public ViewerEffect()
    {
        Effect_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = Effect_Fields.iterator();
        int i;
        for (i = 35; iterator.hasNext(); i = ((Effect)iterator.next()).TypeData.length + 42 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleViewerEffect(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)17);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)Effect_Fields.size());
        Effect effect;
        for (Iterator iterator = Effect_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, effect.TypeData, 1))
        {
            effect = (Effect)iterator.next();
            packUUID(bytebuffer, effect.ID);
            packUUID(bytebuffer, effect.AgentID);
            packByte(bytebuffer, (byte)effect.Type);
            packFloat(bytebuffer, effect.Duration);
            packFixed(bytebuffer, effect.Color, 4);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Effect effect = new Effect();
            effect.ID = unpackUUID(bytebuffer);
            effect.AgentID = unpackUUID(bytebuffer);
            effect.Type = unpackByte(bytebuffer) & 0xff;
            effect.Duration = unpackFloat(bytebuffer);
            effect.Color = unpackFixed(bytebuffer, 4);
            effect.TypeData = unpackVariable(bytebuffer, 1);
            Effect_Fields.add(effect);
        }

    }
}
