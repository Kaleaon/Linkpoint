// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class CrossedRegion extends SLMessage
{
    public AgentData AgentData_Field;
    public Info Info_Field;
    public RegionData RegionData_Field;
    
    public CrossedRegion() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.RegionData_Field = new RegionData();
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RegionData_Field.SeedCapability.length + 16 + 34 + 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleCrossedRegion(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)7);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packIPAddress(byteBuffer, this.RegionData_Field.SimIP);
        this.packShort(byteBuffer, (short)this.RegionData_Field.SimPort);
        this.packLong(byteBuffer, this.RegionData_Field.RegionHandle);
        this.packVariable(byteBuffer, this.RegionData_Field.SeedCapability, 2);
        this.packLLVector3(byteBuffer, this.Info_Field.Position);
        this.packLLVector3(byteBuffer, this.Info_Field.LookAt);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.RegionData_Field.SimIP = this.unpackIPAddress(byteBuffer);
        this.RegionData_Field.SimPort = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.RegionData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.RegionData_Field.SeedCapability = this.unpackVariable(byteBuffer, 2);
        this.Info_Field.Position = this.unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = this.unpackLLVector3(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class Info
    {
        public LLVector3 LookAt;
        public LLVector3 Position;
    }
    
    public static class RegionData
    {
        public long RegionHandle;
        public byte[] SeedCapability;
        public Inet4Address SimIP;
        public int SimPort;
    }
}
