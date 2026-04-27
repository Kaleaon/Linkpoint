package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class RegionPresenceResponse extends SLMessage {
    public ArrayList<RegionData> RegionData_Fields = new ArrayList<>();

    public static class RegionData {
        public Inet4Address ExternalRegionIP;
        public Inet4Address InternalRegionIP;
        public byte[] Message;
        public long RegionHandle;
        public UUID RegionID;
        public int RegionPort;
        public double ValidUntil;
    }

    public RegionPresenceResponse() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        int i = 5;
        Iterator<T> it = this.RegionData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            i = ((RegionData) it.next()).Message.length + 43 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceResponse(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 16);
        byteBuffer.put((byte) this.RegionData_Fields.size());
        for (RegionData regionData : this.RegionData_Fields) {
            packUUID(byteBuffer, regionData.RegionID);
            packLong(byteBuffer, regionData.RegionHandle);
            packIPAddress(byteBuffer, regionData.InternalRegionIP);
            packIPAddress(byteBuffer, regionData.ExternalRegionIP);
            packShort(byteBuffer, (short) regionData.RegionPort);
            packDouble(byteBuffer, regionData.ValidUntil);
            packVariable(byteBuffer, regionData.Message, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RegionData regionData = new RegionData();
            regionData.RegionID = unpackUUID(byteBuffer);
            regionData.RegionHandle = unpackLong(byteBuffer);
            regionData.InternalRegionIP = unpackIPAddress(byteBuffer);
            regionData.ExternalRegionIP = unpackIPAddress(byteBuffer);
            regionData.RegionPort = unpackShort(byteBuffer) & 65535;
            regionData.ValidUntil = unpackDouble(byteBuffer);
            regionData.Message = unpackVariable(byteBuffer, 1);
            this.RegionData_Fields.add(regionData);
        }
    }
}
