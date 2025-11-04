package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class NearestLandingRegionRequest extends SLMessage {
    public RequestingRegionData RequestingRegionData_Field;

    /* loaded from: classes.dex */
    public static class RequestingRegionData {
        public long RegionHandle;
    }

    public NearestLandingRegionRequest() {
        this.zeroCoded = false;
        this.RequestingRegionData_Field = new RequestingRegionData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 12;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNearestLandingRegionRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -112);
        packLong(byteBuffer, this.RequestingRegionData_Field.RegionHandle);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestingRegionData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
