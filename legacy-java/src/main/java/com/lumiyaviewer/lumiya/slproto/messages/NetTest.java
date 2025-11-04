package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

/* loaded from: classes.dex */
public class NetTest extends SLMessage {
    public NetBlock NetBlock_Field;

    /* loaded from: classes.dex */
    public static class NetBlock {
        public int Port;
    }

    public NetTest() {
        this.zeroCoded = false;
        this.NetBlock_Field = new NetBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 6;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNetTest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 70);
        packShort(byteBuffer, (short) this.NetBlock_Field.Port);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.NetBlock_Field.Port = unpackShort(byteBuffer) & 65535;
    }
}
