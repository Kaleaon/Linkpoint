package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class StartPingCheck extends SLMessage {
    public PingID PingID_Field = new PingID();

    public static class PingID {
        public int OldestUnacked;
        public int PingID;
    }

    public StartPingCheck() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 6;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartPingCheck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 1);
        packByte(byteBuffer, (byte) this.PingID_Field.PingID);
        packInt(byteBuffer, this.PingID_Field.OldestUnacked);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.PingID_Field.OldestUnacked = unpackInt(byteBuffer);
    }
}
