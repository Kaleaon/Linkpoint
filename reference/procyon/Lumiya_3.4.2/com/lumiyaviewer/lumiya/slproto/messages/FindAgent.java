// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.net.Inet4Address;
import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class FindAgent extends SLMessage
{
    public AgentBlock AgentBlock_Field;
    public ArrayList<LocationBlock> LocationBlock_Fields;
    
    public FindAgent() {
        this.LocationBlock_Fields = new ArrayList<LocationBlock>();
        this.zeroCoded = false;
        this.AgentBlock_Field = new AgentBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.LocationBlock_Fields.size() * 16 + 41;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleFindAgent(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)0);
        this.packUUID(byteBuffer, this.AgentBlock_Field.Hunter);
        this.packUUID(byteBuffer, this.AgentBlock_Field.Prey);
        this.packIPAddress(byteBuffer, this.AgentBlock_Field.SpaceIP);
        byteBuffer.put((byte)this.LocationBlock_Fields.size());
        for (final LocationBlock locationBlock : this.LocationBlock_Fields) {
            this.packDouble(byteBuffer, locationBlock.GlobalX);
            this.packDouble(byteBuffer, locationBlock.GlobalY);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentBlock_Field.Hunter = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.Prey = this.unpackUUID(byteBuffer);
        this.AgentBlock_Field.SpaceIP = this.unpackIPAddress(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final LocationBlock e = new LocationBlock();
            e.GlobalX = this.unpackDouble(byteBuffer);
            e.GlobalY = this.unpackDouble(byteBuffer);
            this.LocationBlock_Fields.add(e);
        }
    }
    
    public static class AgentBlock
    {
        public UUID Hunter;
        public UUID Prey;
        public Inet4Address SpaceIP;
    }
    
    public static class LocationBlock
    {
        public double GlobalX;
        public double GlobalY;
    }
}
