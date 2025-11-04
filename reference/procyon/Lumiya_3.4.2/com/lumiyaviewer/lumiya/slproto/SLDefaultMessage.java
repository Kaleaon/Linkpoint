// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;

public class SLDefaultMessage extends SLMessage
{
    @Override
    public int CalcPayloadSize() {
        return 0;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.DefaultMessageHandler(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
    }
}
