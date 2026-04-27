// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TransferInfo extends SLMessage
{
    public TransferInfoData TransferInfoData_Field;
    
    public TransferInfo() {
        this.zeroCoded = true;
        this.TransferInfoData_Field = new TransferInfoData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.TransferInfoData_Field.Params.length + 34 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTransferInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-102));
        this.packUUID(byteBuffer, this.TransferInfoData_Field.TransferID);
        this.packInt(byteBuffer, this.TransferInfoData_Field.ChannelType);
        this.packInt(byteBuffer, this.TransferInfoData_Field.TargetType);
        this.packInt(byteBuffer, this.TransferInfoData_Field.Status);
        this.packInt(byteBuffer, this.TransferInfoData_Field.Size);
        this.packVariable(byteBuffer, this.TransferInfoData_Field.Params, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TransferInfoData_Field.TransferID = this.unpackUUID(byteBuffer);
        this.TransferInfoData_Field.ChannelType = this.unpackInt(byteBuffer);
        this.TransferInfoData_Field.TargetType = this.unpackInt(byteBuffer);
        this.TransferInfoData_Field.Status = this.unpackInt(byteBuffer);
        this.TransferInfoData_Field.Size = this.unpackInt(byteBuffer);
        this.TransferInfoData_Field.Params = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class TransferInfoData
    {
        public int ChannelType;
        public byte[] Params;
        public int Size;
        public int Status;
        public int TargetType;
        public UUID TransferID;
    }
}
