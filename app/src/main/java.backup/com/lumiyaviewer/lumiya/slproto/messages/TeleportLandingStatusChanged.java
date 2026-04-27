package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class TeleportLandingStatusChanged extends SLMessage {
    public RegionData RegionData_Field = new RegionData();

    public static class RegionData {
        public long RegionHandle;
    }

    public TeleportLandingStatusChanged() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 12;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLandingStatusChanged(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -109);
        packLong(byteBuffer, this.RegionData_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer);
    }
}
