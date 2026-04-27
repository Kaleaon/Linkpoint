// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptMailRegistration extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public ScriptMailRegistration() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.TargetIP.length + 1 + 2 + 16 + 4 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptMailRegistration(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-94));
        this.packVariable(byteBuffer, this.DataBlock_Field.TargetIP, 1);
        this.packShort(byteBuffer, (short)this.DataBlock_Field.TargetPort);
        this.packUUID(byteBuffer, this.DataBlock_Field.TaskID);
        this.packInt(byteBuffer, this.DataBlock_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.TargetIP = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.TargetPort = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.DataBlock_Field.TaskID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Flags = this.unpackInt(byteBuffer);
    }
    
    public static class DataBlock
    {
        public int Flags;
        public byte[] TargetIP;
        public int TargetPort;
        public UUID TaskID;
    }
}
