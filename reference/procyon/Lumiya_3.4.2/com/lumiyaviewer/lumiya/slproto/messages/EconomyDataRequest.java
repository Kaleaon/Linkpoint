// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EconomyDataRequest extends SLMessage
{
    public EconomyDataRequest() {
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEconomyDataRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)24);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
    }
}
