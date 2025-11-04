// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelPropertiesRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public ParcelData ParcelData_Field;
    
    public ParcelPropertiesRequest() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 55;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelPropertiesRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)11);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        this.packFloat(byteBuffer, this.ParcelData_Field.West);
        this.packFloat(byteBuffer, this.ParcelData_Field.South);
        this.packFloat(byteBuffer, this.ParcelData_Field.East);
        this.packFloat(byteBuffer, this.ParcelData_Field.North);
        this.packBoolean(byteBuffer, this.ParcelData_Field.SnapSelection);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.SequenceID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.West = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.South = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.East = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.North = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.SnapSelection = this.unpackBoolean(byteBuffer);
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
        public int SequenceID;
        public boolean SnapSelection;
        public float South;
        public float West;
    }
}
