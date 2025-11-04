// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarAnimation extends SLMessage
{
    public ArrayList<AnimationList> AnimationList_Fields;
    public ArrayList<AnimationSourceList> AnimationSourceList_Fields;
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields;
    public Sender Sender_Field;
    
    public AvatarAnimation() {
        this.AnimationList_Fields = new ArrayList<AnimationList>();
        this.AnimationSourceList_Fields = new ArrayList<AnimationSourceList>();
        this.PhysicalAvatarEventList_Fields = new ArrayList<PhysicalAvatarEventList>();
        this.zeroCoded = false;
        this.Sender_Field = new Sender();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int size = this.AnimationList_Fields.size();
        final int size2 = this.AnimationSourceList_Fields.size();
        final Iterator<Object> iterator = this.PhysicalAvatarEventList_Fields.iterator();
        int n = size * 20 + 18 + 1 + size2 * 16 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().TypeData.length + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarAnimation(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)20);
        this.packUUID(byteBuffer, this.Sender_Field.ID);
        byteBuffer.put((byte)this.AnimationList_Fields.size());
        for (final AnimationList list : this.AnimationList_Fields) {
            this.packUUID(byteBuffer, list.AnimID);
            this.packInt(byteBuffer, list.AnimSequenceID);
        }
        byteBuffer.put((byte)this.AnimationSourceList_Fields.size());
        final Iterator<Object> iterator2 = this.AnimationSourceList_Fields.iterator();
        while (iterator2.hasNext()) {
            this.packUUID(byteBuffer, iterator2.next().ObjectID);
        }
        byteBuffer.put((byte)this.PhysicalAvatarEventList_Fields.size());
        final Iterator<Object> iterator3 = this.PhysicalAvatarEventList_Fields.iterator();
        while (iterator3.hasNext()) {
            this.packVariable(byteBuffer, iterator3.next().TypeData, 1);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.Sender_Field.ID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final AnimationList e = new AnimationList();
            e.AnimID = this.unpackUUID(byteBuffer);
            e.AnimSequenceID = this.unpackInt(byteBuffer);
            this.AnimationList_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = 0; j < (value2 & 0xFF); ++j) {
            final AnimationSourceList e2 = new AnimationSourceList();
            e2.ObjectID = this.unpackUUID(byteBuffer);
            this.AnimationSourceList_Fields.add(e2);
        }
        final byte value3 = byteBuffer.get();
        for (int k = n; k < (value3 & 0xFF); ++k) {
            final PhysicalAvatarEventList e3 = new PhysicalAvatarEventList();
            e3.TypeData = this.unpackVariable(byteBuffer, 1);
            this.PhysicalAvatarEventList_Fields.add(e3);
        }
    }
    
    public static class AnimationList
    {
        public UUID AnimID;
        public int AnimSequenceID;
    }
    
    public static class AnimationSourceList
    {
        public UUID ObjectID;
    }
    
    public static class PhysicalAvatarEventList
    {
        public byte[] TypeData;
    }
    
    public static class Sender
    {
        public UUID ID;
    }
}
