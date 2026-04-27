// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DerezContainer extends SLMessage
{
    public Data Data_Field;
    
    public DerezContainer() {
        this.zeroCoded = true;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 21;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDerezContainer(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)104);
        this.packUUID(byteBuffer, this.Data_Field.ObjectID);
        this.packBoolean(byteBuffer, this.Data_Field.Delete);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.Data_Field.Delete = this.unpackBoolean(byteBuffer);
    }
    
    public static class Data
    {
        public boolean Delete;
        public UUID ObjectID;
    }
}
