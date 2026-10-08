package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class HealthMessage extends SLMessage {
    public HealthData HealthData_Field;

    /* loaded from: classes.dex */
    public static class HealthData {
        public float Health;
    }

    public HealthMessage() {
        this.zeroCoded = true;
        this.HealthData_Field = new HealthData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 8;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleHealthMessage(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -118);
        packFloat(byteBuffer, this.HealthData_Field.Health);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.HealthData_Field.Health = unpackFloat(byteBuffer);
    }
}
