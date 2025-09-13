package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class NetTest extends SLMessage {
    public NetBlock NetBlock_Field = new NetBlock();

    public static class NetBlock {
        public int Port;
    }

    public NetTest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 6;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNetTest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 70);
        packShort(byteBuffer, (short) this.NetBlock_Field.Port);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.NetBlock_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
