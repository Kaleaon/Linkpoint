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

public class ParcelClaim extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class Data
    {

        public boolean Final;
        public UUID GroupID;
        public boolean IsGroupOwned;

        public Data()
        {
        }
    }

    public static class ParcelData
    {

        public float East;
        public float North;
        public float South;
        public float West;

        public ParcelData()
        {
        }
    }


    public AgentData AgentData_Field;
    public Data Data_Field;
    public ArrayList ParcelData_Fields;

    public ParcelClaim()
    {
        ParcelData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Fields.size() * 16 + 55;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelClaim(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-47);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, Data_Field.GroupID);
        packBoolean(bytebuffer, Data_Field.IsGroupOwned);
        packBoolean(bytebuffer, Data_Field.Final);
        bytebuffer.put((byte)ParcelData_Fields.size());
        ParcelData parceldata;
        for (Iterator iterator = ParcelData_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, parceldata.North))
        {
            parceldata = (ParcelData)iterator.next();
            packFloat(bytebuffer, parceldata.West);
            packFloat(bytebuffer, parceldata.South);
            packFloat(bytebuffer, parceldata.East);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        Data_Field.GroupID = unpackUUID(bytebuffer);
        Data_Field.IsGroupOwned = unpackBoolean(bytebuffer);
        Data_Field.Final = unpackBoolean(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParcelData parceldata = new ParcelData();
            parceldata.West = unpackFloat(bytebuffer);
            parceldata.South = unpackFloat(bytebuffer);
            parceldata.East = unpackFloat(bytebuffer);
            parceldata.North = unpackFloat(bytebuffer);
            ParcelData_Fields.add(parceldata);
        }

    }
}
