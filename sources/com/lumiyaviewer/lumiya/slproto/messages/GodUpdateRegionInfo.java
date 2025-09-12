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

public class GodUpdateRegionInfo extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RegionInfo
    {

        public float BillableFactor;
        public int EstateID;
        public int ParentEstateID;
        public int PricePerMeter;
        public int RedirectGridX;
        public int RedirectGridY;
        public int RegionFlags;
        public byte SimName[];

        public RegionInfo()
        {
        }
    }

    public static class RegionInfo2
    {

        public long RegionFlagsExtended;

        public RegionInfo2()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList RegionInfo2_Fields;
    public RegionInfo RegionInfo_Field;

    public GodUpdateRegionInfo()
    {
        RegionInfo2_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        RegionInfo_Field = new RegionInfo();
    }

    public int CalcPayloadSize()
    {
        return RegionInfo_Field.SimName.length + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 36 + 1 + RegionInfo2_Fields.size() * 8;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleGodUpdateRegionInfo(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-113);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, RegionInfo_Field.SimName, 1);
        packInt(bytebuffer, RegionInfo_Field.EstateID);
        packInt(bytebuffer, RegionInfo_Field.ParentEstateID);
        packInt(bytebuffer, RegionInfo_Field.RegionFlags);
        packFloat(bytebuffer, RegionInfo_Field.BillableFactor);
        packInt(bytebuffer, RegionInfo_Field.PricePerMeter);
        packInt(bytebuffer, RegionInfo_Field.RedirectGridX);
        packInt(bytebuffer, RegionInfo_Field.RedirectGridY);
        bytebuffer.put((byte)RegionInfo2_Fields.size());
        for (Iterator iterator = RegionInfo2_Fields.iterator(); iterator.hasNext(); packLong(bytebuffer, ((RegionInfo2)iterator.next()).RegionFlagsExtended)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RegionInfo_Field.SimName = unpackVariable(bytebuffer, 1);
        RegionInfo_Field.EstateID = unpackInt(bytebuffer);
        RegionInfo_Field.ParentEstateID = unpackInt(bytebuffer);
        RegionInfo_Field.RegionFlags = unpackInt(bytebuffer);
        RegionInfo_Field.BillableFactor = unpackFloat(bytebuffer);
        RegionInfo_Field.PricePerMeter = unpackInt(bytebuffer);
        RegionInfo_Field.RedirectGridX = unpackInt(bytebuffer);
        RegionInfo_Field.RedirectGridY = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionInfo2 regioninfo2 = new RegionInfo2();
            regioninfo2.RegionFlagsExtended = unpackLong(bytebuffer);
            RegionInfo2_Fields.add(regioninfo2);
        }

    }
}
