// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DenyTrustedCircuit extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public DenyTrustedCircuit() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDenyTrustedCircuit(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-119));
        this.packUUID(byteBuffer, this.DataBlock_Field.EndPointID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.EndPointID = this.unpackUUID(byteBuffer);
    }
    
    public static class DataBlock
    {
        public UUID EndPointID;
    }
}
