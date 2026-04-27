// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionHandleRequest extends SLMessage
{
    public RequestBlock RequestBlock_Field;
    
    public RegionHandleRequest() {
        this.zeroCoded = false;
        this.RequestBlock_Field = new RequestBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionHandleRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)53);
        this.packUUID(byteBuffer, this.RequestBlock_Field.RegionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.RequestBlock_Field.RegionID = this.unpackUUID(byteBuffer);
    }
    
    public static class RequestBlock
    {
        public UUID RegionID;
    }
}
