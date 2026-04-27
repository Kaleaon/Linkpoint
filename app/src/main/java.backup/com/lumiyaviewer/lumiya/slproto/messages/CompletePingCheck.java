package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class CompletePingCheck extends SLMessage {
    public PingID PingID_Field = new PingID();

    public static class PingID {
        public int PingID;
    }

    public CompletePingCheck() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 2;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCompletePingCheck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 2);
        packByte(byteBuffer, (byte) this.PingID_Field.PingID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
