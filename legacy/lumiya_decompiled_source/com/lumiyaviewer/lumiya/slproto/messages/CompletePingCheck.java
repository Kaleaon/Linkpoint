package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class CompletePingCheck extends SLMessage {
    public PingID PingID_Field;

    /* loaded from: classes.dex */
    public static class PingID {
        public int PingID;
    }

    public CompletePingCheck() {
        this.zeroCoded = false;
        this.PingID_Field = new PingID();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 2;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCompletePingCheck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 2);
        packByte(byteBuffer, (byte) this.PingID_Field.PingID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
