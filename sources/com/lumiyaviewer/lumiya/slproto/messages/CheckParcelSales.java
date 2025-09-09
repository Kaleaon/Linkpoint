package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class CheckParcelSales extends SLMessage {
    public ArrayList<RegionData> RegionData_Fields = new ArrayList<>();

    public static class RegionData {
        public long RegionHandle;
    }

    public CheckParcelSales() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 8) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCheckParcelSales(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -31);
        byteBuffer.put((byte) this.RegionData_Fields.size());
        for (RegionData regionData : this.RegionData_Fields) {
            packLong(byteBuffer, regionData.RegionHandle);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RegionData regionData = new RegionData();
            regionData.RegionHandle = unpackLong(byteBuffer);
            this.RegionData_Fields.add(regionData);
        }
    }
}
