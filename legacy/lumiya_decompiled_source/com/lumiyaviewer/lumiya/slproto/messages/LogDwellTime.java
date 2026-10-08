package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class LogDwellTime extends SLMessage {
    public DwellInfo DwellInfo_Field;

    /* loaded from: classes.dex */
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
        this.DwellInfo_Field = new DwellInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.DwellInfo_Field.SimName.length + 37 + 4 + 4 + 1 + 1 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLogDwellTime(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
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
