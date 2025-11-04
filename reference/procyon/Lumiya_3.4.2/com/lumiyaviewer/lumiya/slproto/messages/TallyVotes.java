// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TallyVotes extends SLMessage
{
    public TallyVotes() {
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTallyVotes(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)109);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
    }
}
