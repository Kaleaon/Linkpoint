// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DeRezAck extends SLMessage
{
    public TransactionData TransactionData_Field;
    
    public DeRezAck() {
        this.zeroCoded = false;
        this.TransactionData_Field = new TransactionData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 21;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDeRezAck(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)36);
        this.packUUID(byteBuffer, this.TransactionData_Field.TransactionID);
        this.packBoolean(byteBuffer, this.TransactionData_Field.Success);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransactionData_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.TransactionData_Field.Success = this.unpackBoolean(byteBuffer);
    }
    
    public static class TransactionData
    {
        public boolean Success;
        public UUID TransactionID;
    }
}
