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

public class CoarseLocationUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class Index
    {

        public int Prey;
        public int You;

        public Index()
        {
        }
    }

    public static class Location
    {

        public int X;
        public int Y;
        public int Z;

        public Location()
        {
        }
    }


    public ArrayList AgentData_Fields;
    public Index Index_Field;
    public ArrayList Location_Fields;

    public CoarseLocationUpdate()
    {
        Location_Fields = new ArrayList();
        AgentData_Fields = new ArrayList();
        zeroCoded = false;
        Index_Field = new Index();
    }

    public int CalcPayloadSize()
    {
        return Location_Fields.size() * 3 + 3 + 4 + 1 + AgentData_Fields.size() * 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCoarseLocationUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)6);
        bytebuffer.put((byte)Location_Fields.size());
        Location location;
        for (Iterator iterator = Location_Fields.iterator(); iterator.hasNext(); packByte(bytebuffer, (byte)location.Z))
        {
            location = (Location)iterator.next();
            packByte(bytebuffer, (byte)location.X);
            packByte(bytebuffer, (byte)location.Y);
        }

        packShort(bytebuffer, (short)Index_Field.You);
        packShort(bytebuffer, (short)Index_Field.Prey);
        bytebuffer.put((byte)AgentData_Fields.size());
        for (Iterator iterator1 = AgentData_Fields.iterator(); iterator1.hasNext(); packUUID(bytebuffer, ((AgentData)iterator1.next()).AgentID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Location location = new Location();
            location.X = unpackByte(bytebuffer) & 0xff;
            location.Y = unpackByte(bytebuffer) & 0xff;
            location.Z = unpackByte(bytebuffer) & 0xff;
            Location_Fields.add(location);
        }

        Index_Field.You = unpackShort(bytebuffer);
        Index_Field.Prey = unpackShort(bytebuffer);
        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            AgentData agentdata = new AgentData();
            agentdata.AgentID = unpackUUID(bytebuffer);
            AgentData_Fields.add(agentdata);
        }

    }
}
