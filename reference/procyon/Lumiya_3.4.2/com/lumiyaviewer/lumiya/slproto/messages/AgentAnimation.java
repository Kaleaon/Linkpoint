// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentAnimation extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<AnimationList> AnimationList_Fields;
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields;
    
    public AgentAnimation() {
        this.AnimationList_Fields = new ArrayList<AnimationList>();
        this.PhysicalAvatarEventList_Fields = new ArrayList<PhysicalAvatarEventList>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int size = this.AnimationList_Fields.size();
        final Iterator<Object> iterator = this.PhysicalAvatarEventList_Fields.iterator();
        int n = size * 17 + 34 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().TypeData.length + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentAnimation(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)5);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte)this.AnimationList_Fields.size());
        for (final AnimationList list : this.AnimationList_Fields) {
            this.packUUID(byteBuffer, list.AnimID);
            this.packBoolean(byteBuffer, list.StartAnim);
        }
        byteBuffer.put((byte)this.PhysicalAvatarEventList_Fields.size());
        final Iterator<Object> iterator2 = this.PhysicalAvatarEventList_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packVariable(byteBuffer, iterator2.next().TypeData, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AnimationList e = new AnimationList();
            e.AnimID = this.unpackUUID(byteBuffer);
            e.StartAnim = this.unpackBoolean(byteBuffer);
            this.AnimationList_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = n; j < (value2 & 0xFF); ++j) {
            final PhysicalAvatarEventList e2 = new PhysicalAvatarEventList();
            e2.TypeData = this.unpackVariable(byteBuffer, 1);
            this.PhysicalAvatarEventList_Fields.add(e2);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class AnimationList
    {
        public UUID AnimID;
        public boolean StartAnim;
    }
    
    public static class PhysicalAvatarEventList
    {
        public byte[] TypeData;
    }
}
