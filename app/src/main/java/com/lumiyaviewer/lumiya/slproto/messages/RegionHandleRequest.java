package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RegionHandleRequest extends SLMessage {
    public RequestBlock RequestBlock_Field = new RequestBlock();

    public static class RequestBlock {
        public UUID RegionID;
    }

    public RegionHandleRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandleRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 53);
        packUUID(byteBuffer, this.RequestBlock_Field.RegionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestBlock_Field.RegionID = unpackUUID(byteBuffer);
    }
}
