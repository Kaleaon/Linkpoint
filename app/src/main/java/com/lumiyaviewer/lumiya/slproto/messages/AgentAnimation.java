package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class AgentAnimation extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<AnimationList> AnimationList_Fields = new ArrayList<>();
    public ArrayList<PhysicalAvatarEventList> PhysicalAvatarEventList_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class AnimationList {
        public UUID AnimID;
        public boolean StartAnim;
    }

    public static class PhysicalAvatarEventList {
        public byte[] TypeData;
    }

    public AgentAnimation() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int size = (this.AnimationList_Fields.size() * 17) + 34 + 1;
        Iterator<T> it = this.PhysicalAvatarEventList_Fields.iterator();
        while (true) {
            int i = size;
            if (!it.hasNext()) {
                return i;
            }
            size = ((PhysicalAvatarEventList) it.next()).TypeData.length + 1 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentAnimation(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 5);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        byteBuffer.put((byte) this.AnimationList_Fields.size());
        for (AnimationList animationList : this.AnimationList_Fields) {
            packUUID(byteBuffer, animationList.AnimID);
            packBoolean(byteBuffer, animationList.StartAnim);
        }
        byteBuffer.put((byte) this.PhysicalAvatarEventList_Fields.size());
        for (PhysicalAvatarEventList physicalAvatarEventList : this.PhysicalAvatarEventList_Fields) {
            packVariable(byteBuffer, physicalAvatarEventList.TypeData, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            AnimationList animationList = new AnimationList();
            animationList.AnimID = unpackUUID(byteBuffer);
            animationList.StartAnim = unpackBoolean(byteBuffer);
            this.AnimationList_Fields.add(animationList);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            PhysicalAvatarEventList physicalAvatarEventList = new PhysicalAvatarEventList();
            physicalAvatarEventList.TypeData = unpackVariable(byteBuffer, 1);
            this.PhysicalAvatarEventList_Fields.add(physicalAvatarEventList);
        }
    }
}
