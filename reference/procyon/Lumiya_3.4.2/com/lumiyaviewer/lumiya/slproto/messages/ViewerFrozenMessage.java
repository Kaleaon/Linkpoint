// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ViewerFrozenMessage extends SLMessage
{
    public FrozenData FrozenData_Field;
    
    public ViewerFrozenMessage() {
        this.zeroCoded = false;
        this.FrozenData_Field = new FrozenData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 5;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleViewerFrozenMessage(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-119));
        this.packBoolean(byteBuffer, this.FrozenData_Field.Data);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.FrozenData_Field.Data = this.unpackBoolean(byteBuffer);
    }
    
    public static class FrozenData
    {
        public boolean Data;
    }
}
