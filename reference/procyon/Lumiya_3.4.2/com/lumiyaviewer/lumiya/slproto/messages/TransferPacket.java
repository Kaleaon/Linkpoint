// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferPacket extends SLMessage
{
    public TransferData TransferData_Field;
    
    public TransferPacket() {
        this.zeroCoded = false;
        this.TransferData_Field = new TransferData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.TransferData_Field.Data.length + 30 + 1;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferPacket(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)17);
        this.packUUID(byteBuffer, this.TransferData_Field.TransferID);
        this.packInt(byteBuffer, this.TransferData_Field.ChannelType);
        this.packInt(byteBuffer, this.TransferData_Field.Packet);
        this.packInt(byteBuffer, this.TransferData_Field.Status);
        this.packVariable(byteBuffer, this.TransferData_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransferData_Field.TransferID = this.unpackUUID(byteBuffer);
        this.TransferData_Field.ChannelType = this.unpackInt(byteBuffer);
        this.TransferData_Field.Packet = this.unpackInt(byteBuffer);
        this.TransferData_Field.Status = this.unpackInt(byteBuffer);
        this.TransferData_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class TransferData
    {
        public int ChannelType;
        public byte[] Data;
        public int Packet;
        public int Status;
        public UUID TransferID;
    }
}
