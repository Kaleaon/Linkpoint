package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class StartPingCheck extends SLMessage {
    public PingID PingID_Field;

    /* loaded from: classes.dex */
    public static class PingID {
        public int OldestUnacked;
        public int PingID;
    }

    public StartPingCheck() {
        this.zeroCoded = false;
        this.PingID_Field = new PingID();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 6;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartPingCheck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 1);
        packByte(byteBuffer, (byte) this.PingID_Field.PingID);
        packInt(byteBuffer, this.PingID_Field.OldestUnacked);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.PingID_Field.OldestUnacked = unpackInt(byteBuffer);
    }
}
