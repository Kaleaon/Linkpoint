// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionPresenceResponse extends SLMessage
{
    public ArrayList<RegionData> RegionData_Fields;
    
    public RegionPresenceResponse() {
        this.RegionData_Fields = new ArrayList<RegionData>();
        this.zeroCoded = true;
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.RegionData_Fields.iterator();
        int n = 5;
        while (iterator.hasNext()) {
            n += iterator.next().Message.length + 43;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionPresenceResponse(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)16);
        byteBuffer.put((byte)this.RegionData_Fields.size());
        for (final RegionData regionData : this.RegionData_Fields) {
            this.packUUID(byteBuffer, regionData.RegionID);
            this.packLong(byteBuffer, regionData.RegionHandle);
            this.packIPAddress(byteBuffer, regionData.InternalRegionIP);
            this.packIPAddress(byteBuffer, regionData.ExternalRegionIP);
            this.packShort(byteBuffer, (short)regionData.RegionPort);
            this.packDouble(byteBuffer, regionData.ValidUntil);
            this.packVariable(byteBuffer, regionData.Message, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RegionData e = new RegionData();
            e.RegionID = this.unpackUUID(byteBuffer);
            e.RegionHandle = this.unpackLong(byteBuffer);
            e.InternalRegionIP = this.unpackIPAddress(byteBuffer);
            e.ExternalRegionIP = this.unpackIPAddress(byteBuffer);
            e.RegionPort = (this.unpackShort(byteBuffer) & 0xFFFF);
            e.ValidUntil = this.unpackDouble(byteBuffer);
            e.Message = this.unpackVariable(byteBuffer, 1);
            this.RegionData_Fields.add(e);
        }
    }
    
    public static class RegionData
    {
        public Inet4Address ExternalRegionIP;
        public Inet4Address InternalRegionIP;
        public byte[] Message;
        public long RegionHandle;
        public UUID RegionID;
        public int RegionPort;
        public double ValidUntil;
    }
}
