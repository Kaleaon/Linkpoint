// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SimStats extends SLMessage
{
    public static class PidStat
    {

        public int PID;

        public PidStat()
        {
        }
    }

    public static class Region
    {

        public int ObjectCapacity;
        public int RegionFlags;
        public int RegionX;
        public int RegionY;

        public Region()
        {
        }
    }

    public static class RegionInfo
    {

        public long RegionFlagsExtended;

        public RegionInfo()
        {
        }
    }

    public static class Stat
    {

        public int StatID;
        public float StatValue;

        public Stat()
        {
        }
    }


    public PidStat PidStat_Field;
    public ArrayList RegionInfo_Fields;
    public Region Region_Field;
    public ArrayList Stat_Fields;

    public SimStats()
    {
        Stat_Fields = new ArrayList();
        RegionInfo_Fields = new ArrayList();
        zeroCoded = false;
        Region_Field = new Region();
        PidStat_Field = new PidStat();
    }

    public int CalcPayloadSize()
    {
        return Stat_Fields.size() * 8 + 21 + 4 + 1 + RegionInfo_Fields.size() * 8;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSimStats(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-116);
        packInt(bytebuffer, Region_Field.RegionX);
        packInt(bytebuffer, Region_Field.RegionY);
        packInt(bytebuffer, Region_Field.RegionFlags);
        packInt(bytebuffer, Region_Field.ObjectCapacity);
        bytebuffer.put((byte)Stat_Fields.size());
        Stat stat;
        for (Iterator iterator = Stat_Fields.iterator(); iterator.hasNext(); packFloat(bytebuffer, stat.StatValue))
        {
            stat = (Stat)iterator.next();
            packInt(bytebuffer, stat.StatID);
        }

        packInt(bytebuffer, PidStat_Field.PID);
        bytebuffer.put((byte)RegionInfo_Fields.size());
        for (Iterator iterator1 = RegionInfo_Fields.iterator(); iterator1.hasNext(); packLong(bytebuffer, ((RegionInfo)iterator1.next()).RegionFlagsExtended)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        boolean flag = false;
        Region_Field.RegionX = unpackInt(bytebuffer);
        Region_Field.RegionY = unpackInt(bytebuffer);
        Region_Field.RegionFlags = unpackInt(bytebuffer);
        Region_Field.ObjectCapacity = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            Stat stat = new Stat();
            stat.StatID = unpackInt(bytebuffer);
            stat.StatValue = unpackFloat(bytebuffer);
            Stat_Fields.add(stat);
        }

        PidStat_Field.PID = unpackInt(bytebuffer);
        byte0 = bytebuffer.get();
        for (int j = ((flag) ? 1 : 0); j < (byte0 & 0xff); j++)
        {
            RegionInfo regioninfo = new RegionInfo();
            regioninfo.RegionFlagsExtended = unpackLong(bytebuffer);
            RegionInfo_Fields.add(regioninfo);
        }

    }
}
