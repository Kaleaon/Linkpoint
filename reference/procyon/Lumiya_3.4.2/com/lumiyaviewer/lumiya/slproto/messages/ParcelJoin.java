// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelJoin extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    
    public ParcelJoin() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 52;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelJoin(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-46));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packFloat(byteBuffer, this.ParcelData_Field.West);
        this.packFloat(byteBuffer, this.ParcelData_Field.South);
        this.packFloat(byteBuffer, this.ParcelData_Field.East);
        this.packFloat(byteBuffer, this.ParcelData_Field.North);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.West = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.South = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.East = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.North = this.unpackFloat(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class ParcelData
    {
        public float East;
        public float North;
        public float South;
        public float West;
    }
}
