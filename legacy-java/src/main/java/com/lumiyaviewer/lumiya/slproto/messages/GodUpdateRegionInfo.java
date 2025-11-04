package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class GodUpdateRegionInfo extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<RegionInfo2> RegionInfo2_Fields = new ArrayList<>();
    public RegionInfo RegionInfo_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class RegionInfo {
        public float BillableFactor;
        public int EstateID;
        public int ParentEstateID;
        public int PricePerMeter;
        public int RedirectGridX;
        public int RedirectGridY;
        public int RegionFlags;
        public byte[] SimName;
    }

    /* loaded from: classes.dex */
    public static class RegionInfo2 {
        public long RegionFlagsExtended;
    }

    public GodUpdateRegionInfo() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.RegionInfo_Field = new RegionInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.RegionInfo_Field.SimName.length + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 36 + 1 + (this.RegionInfo2_Fields.size() * 8);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGodUpdateRegionInfo(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -113);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packVariable(byteBuffer, this.RegionInfo_Field.SimName, 1);
        packInt(byteBuffer, this.RegionInfo_Field.EstateID);
        packInt(byteBuffer, this.RegionInfo_Field.ParentEstateID);
        packInt(byteBuffer, this.RegionInfo_Field.RegionFlags);
        packFloat(byteBuffer, this.RegionInfo_Field.BillableFactor);
        packInt(byteBuffer, this.RegionInfo_Field.PricePerMeter);
        packInt(byteBuffer, this.RegionInfo_Field.RedirectGridX);
        packInt(byteBuffer, this.RegionInfo_Field.RedirectGridY);
        byteBuffer.put((byte) this.RegionInfo2_Fields.size());
        Iterator<T> it = this.RegionInfo2_Fields.iterator();
        while (it.hasNext()) {
            packLong(byteBuffer, ((RegionInfo2) it.next()).RegionFlagsExtended);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.RegionInfo_Field.SimName = unpackVariable(byteBuffer, 1);
        this.RegionInfo_Field.EstateID = unpackInt(byteBuffer);
        this.RegionInfo_Field.ParentEstateID = unpackInt(byteBuffer);
        this.RegionInfo_Field.RegionFlags = unpackInt(byteBuffer);
        this.RegionInfo_Field.BillableFactor = unpackFloat(byteBuffer);
        this.RegionInfo_Field.PricePerMeter = unpackInt(byteBuffer);
        this.RegionInfo_Field.RedirectGridX = unpackInt(byteBuffer);
        this.RegionInfo_Field.RedirectGridY = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            RegionInfo2 regionInfo2 = new RegionInfo2();
            regionInfo2.RegionFlagsExtended = unpackLong(byteBuffer);
            this.RegionInfo2_Fields.add(regionInfo2);
        }
    }
}
