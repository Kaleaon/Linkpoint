package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class MeanCollisionAlert extends SLMessage {
    public ArrayList<MeanCollision> MeanCollision_Fields = new ArrayList<>();

    public static class MeanCollision {
        public float Mag;
        public UUID Perp;
        public int Time;
        public int Type;
        public UUID Victim;
    }

    public MeanCollisionAlert() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return (this.MeanCollision_Fields.size() * 41) + 5;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleMeanCollisionAlert(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -120);
        byteBuffer.put((byte) this.MeanCollision_Fields.size());
        for (MeanCollision meanCollision : this.MeanCollision_Fields) {
            packUUID(byteBuffer, meanCollision.Victim);
            packUUID(byteBuffer, meanCollision.Perp);
            packInt(byteBuffer, meanCollision.Time);
            packFloat(byteBuffer, meanCollision.Mag);
            packByte(byteBuffer, (byte) meanCollision.Type);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            MeanCollision meanCollision = new MeanCollision();
            meanCollision.Victim = unpackUUID(byteBuffer);
            meanCollision.Perp = unpackUUID(byteBuffer);
            meanCollision.Time = unpackInt(byteBuffer);
            meanCollision.Mag = unpackFloat(byteBuffer);
            meanCollision.Type = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.MeanCollision_Fields.add(meanCollision);
        }
    }
}
