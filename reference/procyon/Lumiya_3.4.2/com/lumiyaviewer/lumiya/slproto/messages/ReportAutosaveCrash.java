// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ReportAutosaveCrash extends SLMessage
{
    public AutosaveData AutosaveData_Field;
    
    public ReportAutosaveCrash() {
        this.zeroCoded = false;
        this.AutosaveData_Field = new AutosaveData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 12;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleReportAutosaveCrash(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-128));
        this.packInt(byteBuffer, this.AutosaveData_Field.PID);
        this.packInt(byteBuffer, this.AutosaveData_Field.Status);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AutosaveData_Field.PID = this.unpackInt(byteBuffer);
        this.AutosaveData_Field.Status = this.unpackInt(byteBuffer);
    }
    
    public static class AutosaveData
    {
        public int PID;
        public int Status;
    }
}
