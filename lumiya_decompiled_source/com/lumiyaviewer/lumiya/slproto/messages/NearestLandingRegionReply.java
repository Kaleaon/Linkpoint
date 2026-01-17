package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class NearestLandingRegionReply extends SLMessage {
    public LandingRegionData LandingRegionData_Field;

    /* loaded from: classes.dex */
    public static class LandingRegionData {
        public long RegionHandle;
    }

    public NearestLandingRegionReply() {
        this.zeroCoded = false;
        this.LandingRegionData_Field = new LandingRegionData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 12;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -111);
        packLong(byteBuffer, this.LandingRegionData_Field.RegionHandle);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.LandingRegionData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
