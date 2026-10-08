package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class ViewerFrozenMessage extends SLMessage {
    public FrozenData FrozenData_Field;

    /* loaded from: classes.dex */
    public static class FrozenData {
        public boolean Data;
    }

    public ViewerFrozenMessage() {
        this.zeroCoded = false;
        this.FrozenData_Field = new FrozenData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleViewerFrozenMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -119);
        packBoolean(byteBuffer, this.FrozenData_Field.Data);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.FrozenData_Field.Data = unpackBoolean(byteBuffer);
    }
}
