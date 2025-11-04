// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferAbort extends SLMessage
{
    public TransferInfo TransferInfo_Field;
    
    public TransferAbort() {
        this.zeroCoded = true;
        this.TransferInfo_Field = new TransferInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferAbort(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-101));
        this.packUUID(byteBuffer, this.TransferInfo_Field.TransferID);
        this.packInt(byteBuffer, this.TransferInfo_Field.ChannelType);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = this.unpackUUID(byteBuffer);
        this.TransferInfo_Field.ChannelType = this.unpackInt(byteBuffer);
    }
    
    public static class TransferInfo
    {
        public int ChannelType;
        public UUID TransferID;
    }
}
