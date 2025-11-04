// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimulatorViewerTimeMessage extends SLMessage
{
    public TimeInfo TimeInfo_Field;
    
    public SimulatorViewerTimeMessage() {
        this.zeroCoded = false;
        this.TimeInfo_Field = new TimeInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 48;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimulatorViewerTimeMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-106));
        this.packLong(byteBuffer, this.TimeInfo_Field.UsecSinceStart);
        this.packInt(byteBuffer, this.TimeInfo_Field.SecPerDay);
        this.packInt(byteBuffer, this.TimeInfo_Field.SecPerYear);
        this.packLLVector3(byteBuffer, this.TimeInfo_Field.SunDirection);
        this.packFloat(byteBuffer, this.TimeInfo_Field.SunPhase);
        this.packLLVector3(byteBuffer, this.TimeInfo_Field.SunAngVelocity);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TimeInfo_Field.UsecSinceStart = this.unpackLong(byteBuffer);
        this.TimeInfo_Field.SecPerDay = this.unpackInt(byteBuffer);
        this.TimeInfo_Field.SecPerYear = this.unpackInt(byteBuffer);
        this.TimeInfo_Field.SunDirection = this.unpackLLVector3(byteBuffer);
        this.TimeInfo_Field.SunPhase = this.unpackFloat(byteBuffer);
        this.TimeInfo_Field.SunAngVelocity = this.unpackLLVector3(byteBuffer);
    }
    
    public static class TimeInfo
    {
        public int SecPerDay;
        public int SecPerYear;
        public LLVector3 SunAngVelocity;
        public LLVector3 SunDirection;
        public float SunPhase;
        public long UsecSinceStart;
    }
}
