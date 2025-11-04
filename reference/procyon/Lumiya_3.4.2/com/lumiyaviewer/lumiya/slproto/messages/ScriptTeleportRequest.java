// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ScriptTeleportRequest extends SLMessage
{
    public Data Data_Field;
    
    public ScriptTeleportRequest() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 1 + this.Data_Field.SimName.length + 12 + 12 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleScriptTeleportRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-61));
        this.packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        this.packVariable(byteBuffer, this.Data_Field.SimName, 1);
        this.packLLVector3(byteBuffer, this.Data_Field.SimPosition);
        this.packLLVector3(byteBuffer, this.Data_Field.LookAt);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.SimName = this.unpackVariable(byteBuffer, 1);
        this.Data_Field.SimPosition = this.unpackLLVector3(byteBuffer);
        this.Data_Field.LookAt = this.unpackLLVector3(byteBuffer);
    }
    
    public static class Data
    {
        public LLVector3 LookAt;
        public byte[] ObjectName;
        public byte[] SimName;
        public LLVector3 SimPosition;
    }
}
