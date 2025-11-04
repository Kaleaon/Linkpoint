// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimStats extends SLMessage
{
    public PidStat PidStat_Field;
    public ArrayList<RegionInfo> RegionInfo_Fields;
    public Region Region_Field;
    public ArrayList<Stat> Stat_Fields;
    
    public SimStats() {
        this.Stat_Fields = new ArrayList<Stat>();
        this.RegionInfo_Fields = new ArrayList<RegionInfo>();
        this.zeroCoded = false;
        this.Region_Field = new Region();
        this.PidStat_Field = new PidStat();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Stat_Fields.size() * 8 + 21 + 4 + 1 + this.RegionInfo_Fields.size() * 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimStats(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-116));
        this.packInt(byteBuffer, this.Region_Field.RegionX);
        this.packInt(byteBuffer, this.Region_Field.RegionY);
        this.packInt(byteBuffer, this.Region_Field.RegionFlags);
        this.packInt(byteBuffer, this.Region_Field.ObjectCapacity);
        byteBuffer.put((byte)this.Stat_Fields.size());
        for (final Stat stat : this.Stat_Fields) {
            this.packInt(byteBuffer, stat.StatID);
            this.packFloat(byteBuffer, stat.StatValue);
        }
        this.packInt(byteBuffer, this.PidStat_Field.PID);
        byteBuffer.put((byte)this.RegionInfo_Fields.size());
        final Iterator<Object> iterator2 = this.RegionInfo_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packLong(byteBuffer, iterator2.next().RegionFlagsExtended);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.Region_Field.RegionX = this.unpackInt(byteBuffer);
        this.Region_Field.RegionY = this.unpackInt(byteBuffer);
        this.Region_Field.RegionFlags = this.unpackInt(byteBuffer);
        this.Region_Field.ObjectCapacity = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final Stat e = new Stat();
            e.StatID = this.unpackInt(byteBuffer);
            e.StatValue = this.unpackFloat(byteBuffer);
            this.Stat_Fields.add(e);
        }
        this.PidStat_Field.PID = this.unpackInt(byteBuffer);
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final RegionInfo e2 = new RegionInfo();
            e2.RegionFlagsExtended = this.unpackLong(byteBuffer);
            this.RegionInfo_Fields.add(e2);
        }
    }
    
    public static class PidStat
    {
        public int PID;
    }
    
    public static class Region
    {
        public int ObjectCapacity;
        public int RegionFlags;
        public int RegionX;
        public int RegionY;
    }
    
    public static class RegionInfo
    {
        public long RegionFlagsExtended;
    }
    
    public static class Stat
    {
        public int StatID;
        public float StatValue;
    }
}
