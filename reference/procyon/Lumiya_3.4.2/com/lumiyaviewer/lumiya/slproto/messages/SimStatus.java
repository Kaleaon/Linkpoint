// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SimStatus extends SLMessage
{
    public SimStatusData SimStatusData_Field;
    
    public SimStatus() {
        this.zeroCoded = false;
        this.SimStatusData_Field = new SimStatusData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSimStatus(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)12);
        this.packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptAgents);
        this.packBoolean(byteBuffer, this.SimStatusData_Field.CanAcceptTasks);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SimStatusData_Field.CanAcceptAgents = this.unpackBoolean(byteBuffer);
        this.SimStatusData_Field.CanAcceptTasks = this.unpackBoolean(byteBuffer);
    }
    
    public static class SimStatusData
    {
        public boolean CanAcceptAgents;
        public boolean CanAcceptTasks;
    }
}
