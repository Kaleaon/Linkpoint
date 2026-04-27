package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class ViewerFrozenMessage extends SLMessage {
    public FrozenData FrozenData_Field = new FrozenData();

    public static class FrozenData {
        public boolean Data;
    }

    public ViewerFrozenMessage() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerFrozenMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -119);
        packBoolean(byteBuffer, this.FrozenData_Field.Data);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.FrozenData_Field.Data = unpackBoolean(byteBuffer);
    }
}
