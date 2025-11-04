// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CreateTrustedCircuit extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public CreateTrustedCircuit() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 52;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCreateTrustedCircuit(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-120));
        this.packUUID(byteBuffer, this.DataBlock_Field.EndPointID);
        this.packFixed(byteBuffer, this.DataBlock_Field.Digest, 32);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.EndPointID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Digest = this.unpackFixed(byteBuffer, 32);
    }
    
    public static class DataBlock
    {
        public byte[] Digest;
        public UUID EndPointID;
    }
}
