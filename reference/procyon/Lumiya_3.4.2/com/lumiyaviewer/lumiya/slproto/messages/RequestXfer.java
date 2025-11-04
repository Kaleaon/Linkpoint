// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RequestXfer extends SLMessage
{
    public XferID XferID_Field;
    
    public RequestXfer() {
        this.zeroCoded = true;
        this.XferID_Field = new XferID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.XferID_Field.Filename.length + 9 + 1 + 1 + 1 + 16 + 2 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRequestXfer(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-100));
        this.packLong(byteBuffer, this.XferID_Field.ID);
        this.packVariable(byteBuffer, this.XferID_Field.Filename, 1);
        this.packByte(byteBuffer, (byte)this.XferID_Field.FilePath);
        this.packBoolean(byteBuffer, this.XferID_Field.DeleteOnCompletion);
        this.packBoolean(byteBuffer, this.XferID_Field.UseBigPackets);
        this.packUUID(byteBuffer, this.XferID_Field.VFileID);
        this.packShort(byteBuffer, (short)this.XferID_Field.VFileType);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.XferID_Field.ID = this.unpackLong(byteBuffer);
        this.XferID_Field.Filename = this.unpackVariable(byteBuffer, 1);
        this.XferID_Field.FilePath = (this.unpackByte(byteBuffer) & 0xFF);
        this.XferID_Field.DeleteOnCompletion = this.unpackBoolean(byteBuffer);
        this.XferID_Field.UseBigPackets = this.unpackBoolean(byteBuffer);
        this.XferID_Field.VFileID = this.unpackUUID(byteBuffer);
        this.XferID_Field.VFileType = this.unpackShort(byteBuffer);
    }
    
    public static class XferID
    {
        public boolean DeleteOnCompletion;
        public int FilePath;
        public byte[] Filename;
        public long ID;
        public boolean UseBigPackets;
        public UUID VFileID;
        public int VFileType;
    }
}
