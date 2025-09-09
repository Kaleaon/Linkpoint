package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class SimStats extends SLMessage {
    public PidStat PidStat_Field;
    public ArrayList<RegionInfo> RegionInfo_Fields = new ArrayList<>();
    public Region Region_Field;
    public ArrayList<Stat> Stat_Fields = new ArrayList<>();

    public static class PidStat {
        public int PID;
    }

    public static class Region {
        public int ObjectCapacity;
        public int RegionFlags;
        public int RegionX;
        public int RegionY;
    }

    public static class RegionInfo {
        public long RegionFlagsExtended;
    }

    public static class Stat {
        public int StatID;
        public float StatValue;
    }

    public SimStats() {
        this.zeroCoded = false;
        this.Region_Field = new Region();
        this.PidStat_Field = new PidStat();
    }

    public int CalcPayloadSize() {
        return (this.Stat_Fields.size() * 8) + 21 + 4 + 1 + (this.RegionInfo_Fields.size() * 8);
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimStats(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -116);
        packInt(byteBuffer, this.Region_Field.RegionX);
        packInt(byteBuffer, this.Region_Field.RegionY);
        packInt(byteBuffer, this.Region_Field.RegionFlags);
        packInt(byteBuffer, this.Region_Field.ObjectCapacity);
        byteBuffer.put((byte) this.Stat_Fields.size());
        for (Stat stat : this.Stat_Fields) {
            packInt(byteBuffer, stat.StatID);
            packFloat(byteBuffer, stat.StatValue);
        }
        packInt(byteBuffer, this.PidStat_Field.PID);
        byteBuffer.put((byte) this.RegionInfo_Fields.size());
        for (RegionInfo regionInfo : this.RegionInfo_Fields) {
            packLong(byteBuffer, regionInfo.RegionFlagsExtended);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Region_Field.RegionX = unpackInt(byteBuffer);
        this.Region_Field.RegionY = unpackInt(byteBuffer);
        this.Region_Field.RegionFlags = unpackInt(byteBuffer);
        this.Region_Field.ObjectCapacity = unpackInt(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            Stat stat = new Stat();
            stat.StatID = unpackInt(byteBuffer);
            stat.StatValue = unpackFloat(byteBuffer);
            this.Stat_Fields.add(stat);
        }
        this.PidStat_Field.PID = unpackInt(byteBuffer);
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            RegionInfo regionInfo = new RegionInfo();
            regionInfo.RegionFlagsExtended = unpackLong(byteBuffer);
            this.RegionInfo_Fields.add(regionInfo);
        }
    }
}
