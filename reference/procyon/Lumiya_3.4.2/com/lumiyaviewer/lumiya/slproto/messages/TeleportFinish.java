// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportFinish extends SLMessage
{
    public Info Info_Field;
    
    public TeleportFinish() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Info_Field.SeedCapability.length + 36 + 1 + 4 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportFinish(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)69);
        this.packUUID(byteBuffer, this.Info_Field.AgentID);
        this.packInt(byteBuffer, this.Info_Field.LocationID);
        this.packIPAddress(byteBuffer, this.Info_Field.SimIP);
        this.packShort(byteBuffer, (short)this.Info_Field.SimPort);
        this.packLong(byteBuffer, this.Info_Field.RegionHandle);
        this.packVariable(byteBuffer, this.Info_Field.SeedCapability, 2);
        this.packByte(byteBuffer, (byte)this.Info_Field.SimAccess);
        this.packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.LocationID = this.unpackInt(byteBuffer);
        this.Info_Field.SimIP = this.unpackIPAddress(byteBuffer);
        this.Info_Field.SimPort = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.Info_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.Info_Field.SeedCapability = this.unpackVariable(byteBuffer, 2);
        this.Info_Field.SimAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.Info_Field.TeleportFlags = this.unpackInt(byteBuffer);
    }
    
    public static class Info
    {
        public UUID AgentID;
        public int LocationID;
        public long RegionHandle;
        public byte[] SeedCapability;
        public int SimAccess;
        public Inet4Address SimIP;
        public int SimPort;
        public int TeleportFlags;
    }
}
