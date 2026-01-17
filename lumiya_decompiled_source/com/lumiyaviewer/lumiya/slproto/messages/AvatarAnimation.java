package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
/* loaded from: classes.dex */
public class AvatarAnimation extends SLMessage {
    public ArrayList<AnimationList> AnimationList_Fields = new ArrayList<>();
    public ArrayList<AnimationSourceList> AnimationSourceList_Fields = new ArrayList<>();
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = new ArrayList<>();
    public Sender Sender_Field;

    /* loaded from: classes.dex */
    public static class AnimationList {
        public UUID AnimID;
        public int AnimSequenceID;
    }

    /* loaded from: classes.dex */
    public static class AnimationSourceList {
        public UUID ObjectID;
    }

    /* loaded from: classes.dex */
    public static class PhysicalAvatarEventList {
        public byte[] TypeData;
    }

    /* loaded from: classes.dex */
    public static class Sender {
        public UUID ID;
    }

    public AvatarAnimation() {
        this.zeroCoded = false;
        this.Sender_Field = new Sender();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int size = (this.AnimationList_Fields.size() * 20) + 18 + 1 + (this.AnimationSourceList_Fields.size() * 16) + 1;
        Iterator<T> it = this.PhysicalAvatarEventList_Fields.iterator();
        while (true) {
            int i = size;
            if (!it.hasNext()) {
                return i;
            }
            size = ((PhysicalAvatarEventList) it.next()).TypeData.length + 1 + i;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarAnimation(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.DC4);
        packUUID(byteBuffer, this.Sender_Field.ID);
        byteBuffer.put((byte) this.AnimationList_Fields.size());
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID);
            packInt(byteBuffer, animationList.AnimSequenceID);
        }
        byteBuffer.put((byte) this.AnimationSourceList_Fields.size());
        for (AnimationSourceList animationSourceList : this.AnimationSourceList_Fields) {
            packUUID(byteBuffer, animationSourceList.ObjectID);
        }
        byteBuffer.put((byte) this.PhysicalAvatarEventList_Fields.size());
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Sender_Field.ID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            AnimationList animationList = new AnimationList();
            animationList.AnimID = unpackUUID(byteBuffer);
            animationList.AnimSequenceID = unpackInt(byteBuffer);
            this.AnimationList_Fields.add(animationList);
        }
        int i3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < i3; i4++) {
            AnimationSourceList animationSourceList = new AnimationSourceList();
            animationSourceList.ObjectID = unpackUUID(byteBuffer);
            this.AnimationSourceList_Fields.add(animationSourceList);
        }
        int i5 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i6 = 0; i6 < i5; i6++) {
            PhysicalAvatarEventList physicalAvatarEventList = new PhysicalAvatarEventList();
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1);
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList);
        }
    }
}
