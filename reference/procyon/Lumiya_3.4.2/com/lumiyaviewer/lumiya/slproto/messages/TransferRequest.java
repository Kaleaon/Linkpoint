// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferRequest extends SLMessage
{
    public TransferInfo TransferInfo_Field;
    
    public TransferRequest() {
        this.zeroCoded = true;
        this.TransferInfo_Field = new TransferInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.TransferInfo_Field.Params.length + 30 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-103));
        this.packUUID(byteBuffer, this.TransferInfo_Field.TransferID);
        this.packInt(byteBuffer, this.TransferInfo_Field.ChannelType);
        this.packInt(byteBuffer, this.TransferInfo_Field.SourceType);
        this.packFloat(byteBuffer, this.TransferInfo_Field.Priority);
        this.packVariable(byteBuffer, this.TransferInfo_Field.Params, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransferInfo_Field.TransferID = this.unpackUUID(byteBuffer);
        this.TransferInfo_Field.ChannelType = this.unpackInt(byteBuffer);
        this.TransferInfo_Field.SourceType = this.unpackInt(byteBuffer);
        this.TransferInfo_Field.Priority = this.unpackFloat(byteBuffer);
        this.TransferInfo_Field.Params = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class TransferInfo
    {
        public int ChannelType;
        public byte[] Params;
        public float Priority;
        public int SourceType;
        public UUID TransferID;
    }
}
