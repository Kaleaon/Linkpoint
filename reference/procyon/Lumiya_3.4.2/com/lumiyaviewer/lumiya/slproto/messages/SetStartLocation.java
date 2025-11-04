// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SetStartLocation extends SLMessage
{
    public StartLocationData StartLocationData_Field;
    
    public SetStartLocation() {
        this.zeroCoded = true;
        this.StartLocationData_Field = new StartLocationData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSetStartLocation(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)69);
        this.packUUID(byteBuffer, this.StartLocationData_Field.AgentID);
        this.packUUID(byteBuffer, this.StartLocationData_Field.RegionID);
        this.packInt(byteBuffer, this.StartLocationData_Field.LocationID);
        this.packLong(byteBuffer, this.StartLocationData_Field.RegionHandle);
        this.packLLVector3(byteBuffer, this.StartLocationData_Field.LocationPos);
        this.packLLVector3(byteBuffer, this.StartLocationData_Field.LocationLookAt);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.StartLocationData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.StartLocationData_Field.RegionID = this.unpackUUID(byteBuffer);
        this.StartLocationData_Field.LocationID = this.unpackInt(byteBuffer);
        this.StartLocationData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.StartLocationData_Field.LocationPos = this.unpackLLVector3(byteBuffer);
        this.StartLocationData_Field.LocationLookAt = this.unpackLLVector3(byteBuffer);
    }
    
    public static class StartLocationData
    {
        public UUID AgentID;
        public int LocationID;
        public LLVector3 LocationLookAt;
        public LLVector3 LocationPos;
        public long RegionHandle;
        public UUID RegionID;
    }
}
