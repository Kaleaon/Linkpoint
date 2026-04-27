package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class LogDwellTime extends SLMessage {
    public DwellInfo DwellInfo_Field = new DwellInfo();

    public static class DwellInfo {
        public UUID AgentID;
        public int AvgAgentsInView;
        public int AvgViewerFPS;
        public float Duration;
        public int RegionX;
        public int RegionY;
        public UUID SessionID;
        public byte[] SimName;
    }

    public LogDwellTime() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DwellInfo_Field.SimName.length + 37 + 4 + 4 + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogDwellTime(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put(Ascii.DC2);
        packUUID(byteBuffer, this.DwellInfo_Field.AgentID);
        packUUID(byteBuffer, this.DwellInfo_Field.SessionID);
        packFloat(byteBuffer, this.DwellInfo_Field.Duration);
        packVariable(byteBuffer, this.DwellInfo_Field.SimName, 1);
        packInt(byteBuffer, this.DwellInfo_Field.RegionX);
        packInt(byteBuffer, this.DwellInfo_Field.RegionY);
        packByte(byteBuffer, (byte) this.DwellInfo_Field.AvgAgentsInView);
        packByte(byteBuffer, (byte) this.DwellInfo_Field.AvgViewerFPS);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DwellInfo_Field.AgentID = unpackUUID(byteBuffer);
        this.DwellInfo_Field.SessionID = unpackUUID(byteBuffer);
        this.DwellInfo_Field.Duration = unpackFloat(byteBuffer);
        this.DwellInfo_Field.SimName = unpackVariable(byteBuffer, 1);
        this.DwellInfo_Field.RegionX = unpackInt(byteBuffer);
        this.DwellInfo_Field.RegionY = unpackInt(byteBuffer);
        this.DwellInfo_Field.AvgAgentsInView = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.DwellInfo_Field.AvgViewerFPS = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
