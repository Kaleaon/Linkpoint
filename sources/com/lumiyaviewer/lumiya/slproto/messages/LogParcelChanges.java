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

public class LogParcelChanges extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;

        public AgentData()
        {
        }
    }

    public static class ParcelData
    {

        public int Action;
        public int ActualArea;
        public boolean IsOwnerGroup;
        public UUID OwnerID;
        public UUID ParcelID;
        public UUID TransactionID;

        public ParcelData()
        {
        }
    }

    public static class RegionData
    {

        public long RegionHandle;

        public RegionData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ParcelData_Fields;
    public RegionData RegionData_Field;

    public LogParcelChanges()
    {
        ParcelData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Fields.size() * 54 + 29;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLogParcelChanges(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-32);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packLong(bytebuffer, RegionData_Field.RegionHandle);
        bytebuffer.put((byte)ParcelData_Fields.size());
        ParcelData parceldata;
        for (Iterator iterator = ParcelData_Fields.iterator(); iterator.hasNext(); packUUID(bytebuffer, parceldata.TransactionID))
        {
            parceldata = (ParcelData)iterator.next();
            packUUID(bytebuffer, parceldata.ParcelID);
            packUUID(bytebuffer, parceldata.OwnerID);
            packBoolean(bytebuffer, parceldata.IsOwnerGroup);
            packInt(bytebuffer, parceldata.ActualArea);
            packByte(bytebuffer, (byte)parceldata.Action);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        RegionData_Field.RegionHandle = unpackLong(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ParcelData parceldata = new ParcelData();
            parceldata.ParcelID = unpackUUID(bytebuffer);
            parceldata.OwnerID = unpackUUID(bytebuffer);
            parceldata.IsOwnerGroup = unpackBoolean(bytebuffer);
            parceldata.ActualArea = unpackInt(bytebuffer);
            parceldata.Action = unpackByte(bytebuffer);
            parceldata.TransactionID = unpackUUID(bytebuffer);
            ParcelData_Fields.add(parceldata);
        }

    }
}
