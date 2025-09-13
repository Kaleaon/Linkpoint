package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class SetCPURatio extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public int Ratio;
    }

    public SetCPURatio() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetCPURatio(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 71);
        packByte(byteBuffer, (byte) this.Data_Field.Ratio);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.Ratio = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
