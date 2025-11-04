// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class LogDwellTime extends SLMessage
{
    public DwellInfo DwellInfo_Field;
    
    public LogDwellTime() {
        this.zeroCoded = false;
        this.DwellInfo_Field = new DwellInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DwellInfo_Field.SimName.length + 37 + 4 + 4 + 1 + 1 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleLogDwellTime(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)18);
        this.packUUID(byteBuffer, this.DwellInfo_Field.AgentID);
        this.packUUID(byteBuffer, this.DwellInfo_Field.SessionID);
        this.packFloat(byteBuffer, this.DwellInfo_Field.Duration);
        this.packVariable(byteBuffer, this.DwellInfo_Field.SimName, 1);
        this.packInt(byteBuffer, this.DwellInfo_Field.RegionX);
        this.packInt(byteBuffer, this.DwellInfo_Field.RegionY);
        this.packByte(byteBuffer, (byte)this.DwellInfo_Field.AvgAgentsInView);
        this.packByte(byteBuffer, (byte)this.DwellInfo_Field.AvgViewerFPS);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DwellInfo_Field.AgentID = this.unpackUUID(byteBuffer);
        this.DwellInfo_Field.SessionID = this.unpackUUID(byteBuffer);
        this.DwellInfo_Field.Duration = this.unpackFloat(byteBuffer);
        this.DwellInfo_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.DwellInfo_Field.RegionX = this.unpackInt(byteBuffer);
        this.DwellInfo_Field.RegionY = this.unpackInt(byteBuffer);
        this.DwellInfo_Field.AvgAgentsInView = (this.unpackByte(byteBuffer) & 0xFF);
        this.DwellInfo_Field.AvgViewerFPS = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class DwellInfo
    {
        public UUID AgentID;
        public int AvgAgentsInView;
        public int AvgViewerFPS;
        public float Duration;
        public int RegionX;
        public int RegionY;
        public UUID SessionID;
        public byte[] SimName;
    }
}
