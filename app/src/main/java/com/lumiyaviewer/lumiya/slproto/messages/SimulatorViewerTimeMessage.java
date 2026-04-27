package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;

public class SimulatorViewerTimeMessage extends SLMessage {
    public TimeInfo TimeInfo_Field = new TimeInfo();

    public static class TimeInfo {
        public int SecPerDay;
        public int SecPerYear;
        public LLVector3 SunAngVelocity;
        public LLVector3 SunDirection;
        public float SunPhase;
        public long UsecSinceStart;
    }

    public SimulatorViewerTimeMessage() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 48;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimulatorViewerTimeMessage(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -106);
        packLong(byteBuffer, this.TimeInfo_Field.UsecSinceStart);
        packInt(byteBuffer, this.TimeInfo_Field.SecPerDay);
        packInt(byteBuffer, this.TimeInfo_Field.SecPerYear);
        packLLVector3(byteBuffer, this.TimeInfo_Field.SunDirection);
        packFloat(byteBuffer, this.TimeInfo_Field.SunPhase);
        packLLVector3(byteBuffer, this.TimeInfo_Field.SunAngVelocity);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TimeInfo_Field.UsecSinceStart = unpackLong(byteBuffer);
        this.TimeInfo_Field.SecPerDay = unpackInt(byteBuffer);
        this.TimeInfo_Field.SecPerYear = unpackInt(byteBuffer);
        this.TimeInfo_Field.SunDirection = unpackLLVector3(byteBuffer);
        this.TimeInfo_Field.SunPhase = unpackFloat(byteBuffer);
        this.TimeInfo_Field.SunAngVelocity = unpackLLVector3(byteBuffer);
    }
}
