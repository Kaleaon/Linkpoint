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

public class ObjectFlagUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public boolean CastsShadows;
        public boolean IsPhantom;
        public boolean IsTemporary;
        public int ObjectLocalID;
        public UUID SessionID;
        public boolean UsePhysics;

        public AgentData()
        {
        }
    }

    public static class ExtraPhysics
    {

        public float Density;
        public float Friction;
        public float GravityMultiplier;
        public int PhysicsShapeType;
        public float Restitution;

        public ExtraPhysics()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ExtraPhysics_Fields;

    public ObjectFlagUpdate()
    {
        ExtraPhysics_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return ExtraPhysics_Fields.size() * 17 + 45;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectFlagUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)94);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packInt(bytebuffer, AgentData_Field.ObjectLocalID);
        packBoolean(bytebuffer, AgentData_Field.UsePhysics);
        packBoolean(bytebuffer, AgentData_Field.IsTemporary);
        packBoolean(bytebuffer, AgentData_Field.IsPhantom);
        packBoolean(bytebuffer, AgentData_Field.CastsShadows);
        bytebuffer.put((byte)ExtraPhysics_Fields.size());
        ExtraPhysics extraphysics;
        for (Iterator iterator = ExtraPhysics_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, extraphysics.GravityMultiplier))
        {
            extraphysics = (ExtraPhysics)iterator.next();
            packByte(bytebuffer, (byte)extraphysics.PhysicsShapeType);
            packFloat(bytebuffer, extraphysics.Density);
            packFloat(bytebuffer, extraphysics.Friction);
            packFloat(bytebuffer, extraphysics.Restitution);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.ObjectLocalID = unpackInt(bytebuffer);
        AgentData_Field.UsePhysics = unpackBoolean(bytebuffer);
        AgentData_Field.IsTemporary = unpackBoolean(bytebuffer);
        AgentData_Field.IsPhantom = unpackBoolean(bytebuffer);
        AgentData_Field.CastsShadows = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ExtraPhysics extraphysics = new ExtraPhysics();
            extraphysics.PhysicsShapeType = unpackByte(bytebuffer) & 0xff;
            extraphysics.Density = unpackFloat(bytebuffer);
            extraphysics.Friction = unpackFloat(bytebuffer);
            extraphysics.Restitution = unpackFloat(bytebuffer);
            extraphysics.GravityMultiplier = unpackFloat(bytebuffer);
            ExtraPhysics_Fields.add(extraphysics);
        }

    }
}
