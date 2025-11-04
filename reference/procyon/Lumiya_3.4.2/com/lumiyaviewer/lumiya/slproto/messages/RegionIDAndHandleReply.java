// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RegionIDAndHandleReply extends SLMessage
{
    public ReplyBlock ReplyBlock_Field;
    
    public RegionIDAndHandleReply() {
        this.zeroCoded = false;
        this.ReplyBlock_Field = new ReplyBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 28;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRegionIDAndHandleReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)54);
        this.packUUID(byteBuffer, this.ReplyBlock_Field.RegionID);
        this.packLong(byteBuffer, this.ReplyBlock_Field.RegionHandle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ReplyBlock_Field.RegionID = this.unpackUUID(byteBuffer);
        this.ReplyBlock_Field.RegionHandle = this.unpackLong(byteBuffer);
    }
    
    public static class ReplyBlock
    {
        public long RegionHandle;
        public UUID RegionID;
    }
}
