// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class NearestLandingRegionReply extends SLMessage
{
    public LandingRegionData LandingRegionData_Field;
    
    public NearestLandingRegionReply() {
        this.zeroCoded = false;
        this.LandingRegionData_Field = new LandingRegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleNearestLandingRegionReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-111));
        this.packLong(byteBuffer, this.LandingRegionData_Field.RegionHandle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.LandingRegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
    }
    
    public static class LandingRegionData
    {
        public long RegionHandle;
    }
}
