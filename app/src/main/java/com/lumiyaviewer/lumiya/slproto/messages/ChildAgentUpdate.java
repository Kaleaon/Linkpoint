package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ChildAgentUpdate extends SLMessage {
    public ArrayList<AgentAccess> AgentAccess_Fields = new ArrayList<>();
    public AgentData AgentData_Field;
    public ArrayList<AgentInfo> AgentInfo_Fields = new ArrayList<>();
    public ArrayList<AnimationData> AnimationData_Fields = new ArrayList<>();
    public ArrayList<GranterBlock> GranterBlock_Fields = new ArrayList<>();
    public ArrayList<GroupData> GroupData_Fields = new ArrayList<>();
    public ArrayList<NVPairData> NVPairData_Fields = new ArrayList<>();
    public ArrayList<VisualParam> VisualParam_Fields = new ArrayList<>();

    public static class AgentAccess {
        public int AgentLegacyAccess;
        public int AgentMaxAccess;
    }

    public static class AgentData {
        public UUID ActiveGroupID;
        public int AgentAccess;
        public UUID AgentID;
        public LLVector3 AgentPos;
        public byte[] AgentTextures;
        public LLVector3 AgentVel;
        public boolean AlwaysRun;
        public float Aspect;
        public LLVector3 AtAxis;
        public LLQuaternion BodyRotation;
        public LLVector3 Center;
        public boolean ChangedGrid;
        public int ControlFlags;
        public float EnergyLevel;
        public float Far;
        public int GodLevel;
        public LLQuaternion HeadRotation;
        public LLVector3 LeftAxis;
        public int LocomotionState;
        public UUID PreyAgent;
        public long RegionHandle;
        public UUID SessionID;
        public LLVector3 Size;
        public byte[] Throttles;
        public LLVector3 UpAxis;
        public int ViewerCircuitCode;
    }

    public static class AgentInfo {
        public int Flags;
    }

    public static class AnimationData {
        public UUID Animation;
        public UUID ObjectID;
    }

    public static class GranterBlock {
        public UUID GranterID;
    }

    public static class GroupData {
        public boolean AcceptNotices;
        public UUID GroupID;
        public long GroupPowers;
    }

    public static class NVPairData {
        public byte[] NVPairs;
    }

    public static class VisualParam {
        public int ParamValue;
    }

    public ChildAgentUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int length = this.AgentData_Field.Throttles.length + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + this.AgentData_Field.AgentTextures.length + 16 + 1 + 1 + (this.GroupData_Fields.size() * 25) + 1 + (this.AnimationData_Fields.size() * 32) + 1 + (this.GranterBlock_Fields.size() * 16) + 1;
        Iterator<T> it = this.NVPairData_Fields.iterator();
        while (true) {
            int i = length;
            if (!it.hasNext()) {
                return i + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AgentAccess_Fields.size() * 2) + 1 + (this.AgentInfo_Fields.size() * 4);
            }
            length = ((NVPairData) it.next()).NVPairs.length + 2 + i;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChildAgentUpdate(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.EM);
        packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packLLVector3(byteBuffer, this.AgentData_Field.AgentPos);
        packLLVector3(byteBuffer, this.AgentData_Field.AgentVel);
        packLLVector3(byteBuffer, this.AgentData_Field.Center);
        packLLVector3(byteBuffer, this.AgentData_Field.Size);
        packLLVector3(byteBuffer, this.AgentData_Field.AtAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis);
        packLLVector3(byteBuffer, this.AgentData_Field.UpAxis);
        packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid);
        packFloat(byteBuffer, this.AgentData_Field.Far);
        packFloat(byteBuffer, this.AgentData_Field.Aspect);
        packVariable(byteBuffer, this.AgentData_Field.Throttles, 1);
        packInt(byteBuffer, this.AgentData_Field.LocomotionState);
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation);
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation);
        packInt(byteBuffer, this.AgentData_Field.ControlFlags);
        packFloat(byteBuffer, this.AgentData_Field.EnergyLevel);
        packByte(byteBuffer, (byte) this.AgentData_Field.GodLevel);
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun);
        packUUID(byteBuffer, this.AgentData_Field.PreyAgent);
        packByte(byteBuffer, (byte) this.AgentData_Field.AgentAccess);
        packVariable(byteBuffer, this.AgentData_Field.AgentTextures, 2);
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID);
        byteBuffer.put((byte) this.GroupData_Fields.size());
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID);
            packLong(byteBuffer, groupData.GroupPowers);
            packBoolean(byteBuffer, groupData.AcceptNotices);
        }
        byteBuffer.put((byte) this.AnimationData_Fields.size());
        for (AnimationData animationData : this.AnimationData_Fields) {
            packUUID(byteBuffer, animationData.Animation);
            packUUID(byteBuffer, animationData.ObjectID);
        }
        byteBuffer.put((byte) this.GranterBlock_Fields.size());
        for (GranterBlock granterBlock : this.GranterBlock_Fields) {
            packUUID(byteBuffer, granterBlock.GranterID);
        }
        byteBuffer.put((byte) this.NVPairData_Fields.size());
        for (NVPairData nVPairData : this.NVPairData_Fields) {
            packVariable(byteBuffer, nVPairData.NVPairs, 2);
        }
        byteBuffer.put((byte) this.VisualParam_Fields.size());
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (byte) visualParam.ParamValue);
        }
        byteBuffer.put((byte) this.AgentAccess_Fields.size());
        for (AgentAccess agentAccess : this.AgentAccess_Fields) {
            packByte(byteBuffer, (byte) agentAccess.AgentLegacyAccess);
            packByte(byteBuffer, (byte) agentAccess.AgentMaxAccess);
        }
        byteBuffer.put((byte) this.AgentInfo_Fields.size());
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packInt(byteBuffer, agentInfo.Flags);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.AgentPos = unpackLLVector3(byteBuffer);
        this.AgentData_Field.AgentVel = unpackLLVector3(byteBuffer);
        this.AgentData_Field.Center = unpackLLVector3(byteBuffer);
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer);
        this.AgentData_Field.AtAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.LeftAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.UpAxis = unpackLLVector3(byteBuffer);
        this.AgentData_Field.ChangedGrid = unpackBoolean(byteBuffer);
        this.AgentData_Field.Far = unpackFloat(byteBuffer);
        this.AgentData_Field.Aspect = unpackFloat(byteBuffer);
        this.AgentData_Field.Throttles = unpackVariable(byteBuffer, 1);
        this.AgentData_Field.LocomotionState = unpackInt(byteBuffer);
        this.AgentData_Field.HeadRotation = unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.BodyRotation = unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.ControlFlags = unpackInt(byteBuffer);
        this.AgentData_Field.EnergyLevel = unpackFloat(byteBuffer);
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer);
        this.AgentData_Field.PreyAgent = unpackUUID(byteBuffer);
        this.AgentData_Field.AgentAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.AgentData_Field.AgentTextures = unpackVariable(byteBuffer, 2);
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            GroupData groupData = new GroupData();
            groupData.GroupID = unpackUUID(byteBuffer);
            groupData.GroupPowers = unpackLong(byteBuffer);
            groupData.AcceptNotices = unpackBoolean(byteBuffer);
            this.GroupData_Fields.add(groupData);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            AnimationData animationData = new AnimationData();
            animationData.Animation = unpackUUID(byteBuffer);
            animationData.ObjectID = unpackUUID(byteBuffer);
            this.AnimationData_Fields.add(animationData);
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i3 = 0; i3 < b3; i3++) {
            GranterBlock granterBlock = new GranterBlock();
            granterBlock.GranterID = unpackUUID(byteBuffer);
            this.GranterBlock_Fields.add(granterBlock);
        }
        byte b4 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i4 = 0; i4 < b4; i4++) {
            NVPairData nVPairData = new NVPairData();
            nVPairData.NVPairs = unpackVariable(byteBuffer, 2);
            this.NVPairData_Fields.add(nVPairData);
        }
        byte b5 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i5 = 0; i5 < b5; i5++) {
            VisualParam visualParam = new VisualParam();
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.VisualParam_Fields.add(visualParam);
        }
        byte b6 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i6 = 0; i6 < b6; i6++) {
            AgentAccess agentAccess = new AgentAccess();
            agentAccess.AgentLegacyAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            agentAccess.AgentMaxAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            this.AgentAccess_Fields.add(agentAccess);
        }
        byte b7 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i7 = 0; i7 < b7; i7++) {
            AgentInfo agentInfo = new AgentInfo();
            agentInfo.Flags = unpackInt(byteBuffer);
            this.AgentInfo_Fields.add(agentInfo);
        }
    }
}
