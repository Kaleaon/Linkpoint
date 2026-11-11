package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class SetCPURatio extends SLMessage {
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class Data {
        public int Ratio;
    }

    public SetCPURatio() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 5;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetCPURatio(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 71);
        packByte(byteBuffer, (byte) this.Data_Field.Ratio);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.Ratio = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
