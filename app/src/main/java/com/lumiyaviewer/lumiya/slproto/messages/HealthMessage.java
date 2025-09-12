package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

public class HealthMessage extends SLMessage {
    public HealthData HealthData_Field = new HealthData();

    public static class HealthData {
        public float Health;
    }

    public HealthMessage() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 8;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleHealthMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -118);
        packFloat(byteBuffer, this.HealthData_Field.Health);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.HealthData_Field.Health = unpackFloat(byteBuffer);
    }
}
