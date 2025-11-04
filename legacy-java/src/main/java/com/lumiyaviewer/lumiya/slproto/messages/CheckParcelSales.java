package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class CheckParcelSales extends SLMessage {
    public ArrayList<RegionData> RegionData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class RegionData {
        public long RegionHandle;
    }

    public CheckParcelSales() {
        this.zeroCoded = false;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.RegionData_Fields.size() * 8) + 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCheckParcelSales(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -31);
        byteBuffer.put((byte) this.RegionData_Fields.size());
        Iterator<T> it = this.RegionData_Fields.iterator();
        while (it.hasNext()) {
            packLong(byteBuffer, ((RegionData) it.next()).RegionHandle);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            RegionData regionData = new RegionData();
            regionData.RegionHandle = unpackLong(byteBuffer);
            this.RegionData_Fields.add(regionData);
        }
    }
}
