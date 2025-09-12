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

public class RegionInfo extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class RegionInfo2
    {

        public int HardMaxAgents;
        public int HardMaxObjects;
        public int MaxAgents32;
        public byte ProductName[];
        public byte ProductSKU[];

        public RegionInfo2()
        {
        }
    }

    public static class RegionInfo3
    {

        public long RegionFlagsExtended;

        public RegionInfo3()
        {
        }
    }

    public static class RegionInfoData
    {

        public float BillableFactor;
        public int EstateID;
        public int MaxAgents;
        public float ObjectBonusFactor;
        public int ParentEstateID;
        public int PricePerMeter;
        public int RedirectGridX;
        public int RedirectGridY;
        public int RegionFlags;
        public int SimAccess;
        public byte SimName[];
        public float SunHour;
        public float TerrainLowerLimit;
        public float TerrainRaiseLimit;
        public boolean UseEstateSun;
        public float WaterHeight;

        public RegionInfoData()
        {
        }
    }


    public AgentData AgentData_Field;
    public RegionInfo2 RegionInfo2_Field;
    public ArrayList RegionInfo3_Fields;
    public RegionInfoData RegionInfoData_Field;

    public RegionInfo()
    {
        RegionInfo3_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        RegionInfoData_Field = new RegionInfoData();
        RegionInfo2_Field = new RegionInfo2();
    }

    public int CalcPayloadSize()
    {
        return RegionInfoData_Field.SimName.length + 1 + 4 + 4 + 4 + 1 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + 4 + 36 + (RegionInfo2_Field.ProductSKU.length + 1 + 1 + RegionInfo2_Field.ProductName.length + 4 + 4 + 4) + 1 + RegionInfo3_Fields.size() * 8;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionInfo(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-114);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packVariable(bytebuffer, RegionInfoData_Field.SimName, 1);
        packInt(bytebuffer, RegionInfoData_Field.EstateID);
        packInt(bytebuffer, RegionInfoData_Field.ParentEstateID);
        packInt(bytebuffer, RegionInfoData_Field.RegionFlags);
        packByte(bytebuffer, (byte)RegionInfoData_Field.SimAccess);
        packByte(bytebuffer, (byte)RegionInfoData_Field.MaxAgents);
        packFloat(bytebuffer, RegionInfoData_Field.BillableFactor);
        packFloat(bytebuffer, RegionInfoData_Field.ObjectBonusFactor);
        packFloat(bytebuffer, RegionInfoData_Field.WaterHeight);
        packFloat(bytebuffer, RegionInfoData_Field.TerrainRaiseLimit);
        packFloat(bytebuffer, RegionInfoData_Field.TerrainLowerLimit);
        packInt(bytebuffer, RegionInfoData_Field.PricePerMeter);
        packInt(bytebuffer, RegionInfoData_Field.RedirectGridX);
        packInt(bytebuffer, RegionInfoData_Field.RedirectGridY);
        packBoolean(bytebuffer, RegionInfoData_Field.UseEstateSun);
        packFloat(bytebuffer, RegionInfoData_Field.SunHour);
        packVariable(bytebuffer, RegionInfo2_Field.ProductSKU, 1);
        packVariable(bytebuffer, RegionInfo2_Field.ProductName, 1);
        packInt(bytebuffer, RegionInfo2_Field.MaxAgents32);
        packInt(bytebuffer, RegionInfo2_Field.HardMaxAgents);
        packInt(bytebuffer, RegionInfo2_Field.HardMaxObjects);
        bytebuffer.put((byte)RegionInfo3_Fields.size());
        for (Iterator iterator = RegionInfo3_Fields.iterator(); iterator.hasNext(); packLong(bytebuffer, ((RegionInfo3)iterator.next()).RegionFlagsExtended)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        RegionInfoData_Field.SimName = unpackVariable(bytebuffer, 1);
        RegionInfoData_Field.EstateID = unpackInt(bytebuffer);
        RegionInfoData_Field.ParentEstateID = unpackInt(bytebuffer);
        RegionInfoData_Field.RegionFlags = unpackInt(bytebuffer);
        RegionInfoData_Field.SimAccess = unpackByte(bytebuffer) & 0xff;
        RegionInfoData_Field.MaxAgents = unpackByte(bytebuffer) & 0xff;
        RegionInfoData_Field.BillableFactor = unpackFloat(bytebuffer);
        RegionInfoData_Field.ObjectBonusFactor = unpackFloat(bytebuffer);
        RegionInfoData_Field.WaterHeight = unpackFloat(bytebuffer);
        RegionInfoData_Field.TerrainRaiseLimit = unpackFloat(bytebuffer);
        RegionInfoData_Field.TerrainLowerLimit = unpackFloat(bytebuffer);
        RegionInfoData_Field.PricePerMeter = unpackInt(bytebuffer);
        RegionInfoData_Field.RedirectGridX = unpackInt(bytebuffer);
        RegionInfoData_Field.RedirectGridY = unpackInt(bytebuffer);
        RegionInfoData_Field.UseEstateSun = unpackBoolean(bytebuffer);
        RegionInfoData_Field.SunHour = unpackFloat(bytebuffer);
        RegionInfo2_Field.ProductSKU = unpackVariable(bytebuffer, 1);
        RegionInfo2_Field.ProductName = unpackVariable(bytebuffer, 1);
        RegionInfo2_Field.MaxAgents32 = unpackInt(bytebuffer);
        RegionInfo2_Field.HardMaxAgents = unpackInt(bytebuffer);
        RegionInfo2_Field.HardMaxObjects = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            RegionInfo3 regioninfo3 = new RegionInfo3();
            regioninfo3.RegionFlagsExtended = unpackLong(bytebuffer);
            RegionInfo3_Fields.add(regioninfo3);
        }

    }
}
