// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InitiateDownload extends SLMessage
{
    public AgentData AgentData_Field;
    public FileData FileData_Field;
    
    public InitiateDownload() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FileData_Field = new FileData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.FileData_Field.SimFilename.length + 1 + 1 + this.FileData_Field.ViewerFilename.length + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInitiateDownload(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-109));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packVariable(byteBuffer, this.FileData_Field.SimFilename, 1);
        this.packVariable(byteBuffer, this.FileData_Field.ViewerFilename, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.FileData_Field.SimFilename = this.unpackVariable(byteBuffer, 1);
        this.FileData_Field.ViewerFilename = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class FileData
    {
        public byte[] SimFilename;
        public byte[] ViewerFilename;
    }
}
