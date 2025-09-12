package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class NearestLandingRegionRequest extends SLMessage {
    public RequestingRegionData RequestingRegionData_Field = new RequestingRegionData();

    public static class RequestingRegionData {
        public long RegionHandle;
    }

    public NearestLandingRegionRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -112);
        packLong(byteBuffer, this.RequestingRegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestingRegionData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
