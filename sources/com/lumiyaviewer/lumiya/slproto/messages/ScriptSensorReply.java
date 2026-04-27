// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ScriptSensorReply extends SLMessage
{
    public static class Requester
    {

        public UUID SourceID;

        public Requester()
        {
        }
    }

    public static class SensedData
    {

        public UUID GroupID;
        public byte Name[];
        public UUID ObjectID;
        public UUID OwnerID;
        public LLVector3 Position;
        public float Range;
        public LLQuaternion Rotation;
        public int Type;
        public LLVector3 Velocity;

        public SensedData()
        {
        }
    }


    public Requester Requester_Field;
    public ArrayList SensedData_Fields;

    public ScriptSensorReply()
    {
        SensedData_Fields = new ArrayList();
        zeroCoded = true;
        Requester_Field = new Requester();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = SensedData_Fields.iterator();
        int i;
        for (i = 21; iterator.hasNext(); i = ((SensedData)iterator.next()).Name.length + 85 + 4 + 4 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleScriptSensorReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-8);
        packUUID(bytebuffer, Requester_Field.SourceID);
        bytebuffer.put((byte)SensedData_Fields.size());
        SensedData senseddata;
        for (Iterator iterator = SensedData_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, senseddata.Range))
        {
            senseddata = (SensedData)iterator.next();
            packUUID(bytebuffer, senseddata.ObjectID);
            packUUID(bytebuffer, senseddata.OwnerID);
            packUUID(bytebuffer, senseddata.GroupID);
            packLLVector3(bytebuffer, senseddata.Position);
            packLLVector3(bytebuffer, senseddata.Velocity);
            packLLQuaternion(bytebuffer, senseddata.Rotation);
            packVariable(bytebuffer, senseddata.Name, 1);
            packInt(bytebuffer, senseddata.Type);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Requester_Field.SourceID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            SensedData senseddata = new SensedData();
            senseddata.ObjectID = unpackUUID(bytebuffer);
            senseddata.OwnerID = unpackUUID(bytebuffer);
            senseddata.GroupID = unpackUUID(bytebuffer);
            senseddata.Position = unpackLLVector3(bytebuffer);
            senseddata.Velocity = unpackLLVector3(bytebuffer);
            senseddata.Rotation = unpackLLQuaternion(bytebuffer);
            senseddata.Name = unpackVariable(bytebuffer, 1);
            senseddata.Type = unpackInt(bytebuffer);
            senseddata.Range = unpackFloat(bytebuffer);
            SensedData_Fields.add(senseddata);
        }

    }
}
