package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class RegionPresenceRequestByRegionID extends SLMessage {
    public ArrayList<RegionData> RegionData_Fields = new ArrayList<>();

    public static class RegionData {
        public UUID RegionID;
    }

    public RegionPresenceRequestByRegionID() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 16) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionPresenceRequestByRegionID(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.SO);
        byteBuffer.put((byte) this.RegionData_Fields.size());
        for (RegionData regionData : this.RegionData_Fields) {
            packUUID(byteBuffer, regionData.RegionID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RegionData regionData = new RegionData();
            regionData.RegionID = unpackUUID(byteBuffer);
            this.RegionData_Fields.add(regionData);
        }
    }
}
