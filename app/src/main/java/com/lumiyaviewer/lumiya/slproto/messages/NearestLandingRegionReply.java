package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class NearestLandingRegionReply extends SLMessage {
    public LandingRegionData LandingRegionData_Field = new LandingRegionData();

    public static class LandingRegionData {
        public long RegionHandle;
    }

    public NearestLandingRegionReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -111);
        packLong(byteBuffer, this.LandingRegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.LandingRegionData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
