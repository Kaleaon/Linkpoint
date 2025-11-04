// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportLandingStatusChanged extends SLMessage
{
    public RegionData RegionData_Field;
    
    public TeleportLandingStatusChanged() {
        this.zeroCoded = false;
        this.RegionData_Field = new RegionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportLandingStatusChanged(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-109));
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
    }
    
    public static class RegionData
    {
        public long RegionHandle;
    }
}
