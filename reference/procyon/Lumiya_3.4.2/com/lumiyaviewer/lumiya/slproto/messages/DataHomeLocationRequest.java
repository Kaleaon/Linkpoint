// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class DataHomeLocationRequest extends SLMessage
{
    public AgentInfo AgentInfo_Field;
    public Info Info_Field;
    
    public DataHomeLocationRequest() {
        this.zeroCoded = true;
        this.Info_Field = new Info();
        this.AgentInfo_Field = new AgentInfo();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 28;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleDataHomeLocationRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)67);
        this.packUUID(byteBuffer, this.Info_Field.AgentID);
        this.packInt(byteBuffer, this.Info_Field.KickedFromEstateID);
        this.packInt(byteBuffer, this.AgentInfo_Field.AgentEffectiveMaturity);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.KickedFromEstateID = this.unpackInt(byteBuffer);
        this.AgentInfo_Field.AgentEffectiveMaturity = this.unpackInt(byteBuffer);
    }
    
    public static class AgentInfo
    {
        public int AgentEffectiveMaturity;
    }
    
    public static class Info
    {
        public UUID AgentID;
        public int KickedFromEstateID;
    }
}
