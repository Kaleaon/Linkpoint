// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class NearestLandingRegionRequest extends SLMessage
{
    public RequestingRegionData RequestingRegionData_Field;
    
    public NearestLandingRegionRequest() {
        this.zeroCoded = false;
        this.RequestingRegionData_Field = new RequestingRegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleNearestLandingRegionRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-112));
        this.packLong(byteBuffer, this.RequestingRegionData_Field.RegionHandle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RequestingRegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
    }
    
    public static class RequestingRegionData
    {
        public long RegionHandle;
    }
}
