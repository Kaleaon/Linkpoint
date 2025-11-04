// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChildAgentUpdate extends SLMessage
{
    public ArrayList<AgentAccess> AgentAccess_Fields;
    public AgentData AgentData_Field;
    public ArrayList<AgentInfo> AgentInfo_Fields;
    public ArrayList<AnimationData> AnimationData_Fields;
    public ArrayList<GranterBlock> GranterBlock_Fields;
    public ArrayList<GroupData> GroupData_Fields;
    public ArrayList<NVPairData> NVPairData_Fields;
    public ArrayList<VisualParam> VisualParam_Fields;
    
    public ChildAgentUpdate() {
        this.GroupData_Fields = new ArrayList<GroupData>();
        this.AnimationData_Fields = new ArrayList<AnimationData>();
        this.GranterBlock_Fields = new ArrayList<GranterBlock>();
        this.NVPairData_Fields = new ArrayList<NVPairData>();
        this.VisualParam_Fields = new ArrayList<VisualParam>();
        this.AgentAccess_Fields = new ArrayList<AgentAccess>();
        this.AgentInfo_Fields = new ArrayList<AgentInfo>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final int length = this.AgentData_Field.Throttles.length;
        final int length2 = this.AgentData_Field.AgentTextures.length;
        final int size = this.GroupData_Fields.size();
        final int size2 = this.AnimationData_Fields.size();
        final int size3 = this.GranterBlock_Fields.size();
        final Iterator<Object> iterator = this.NVPairData_Fields.iterator();
        int n = length + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + length2 + 16 + 1 + 1 + size * 25 + 1 + size2 * 32 + 1 + size3 * 16 + 1;
        while (iterator.hasNext()) {
            n += iterator.next().NVPairs.length + 2;
        }
        return n + 1 + this.VisualParam_Fields.size() * 1 + 1 + this.AgentAccess_Fields.size() * 2 + 1 + this.AgentInfo_Fields.size() * 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChildAgentUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)25);
        this.packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        this.packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AgentPos);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AgentVel);
        this.packLLVector3(byteBuffer, this.AgentData_Field.Center);
        this.packLLVector3(byteBuffer, this.AgentData_Field.Size);
        this.packLLVector3(byteBuffer, this.AgentData_Field.AtAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis);
        this.packLLVector3(byteBuffer, this.AgentData_Field.UpAxis);
        this.packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid);
        this.packFloat(byteBuffer, this.AgentData_Field.Far);
        this.packFloat(byteBuffer, this.AgentData_Field.Aspect);
        this.packVariable(byteBuffer, this.AgentData_Field.Throttles, 1);
        this.packInt(byteBuffer, this.AgentData_Field.LocomotionState);
        this.packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation);
        this.packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation);
        this.packInt(byteBuffer, this.AgentData_Field.ControlFlags);
        this.packFloat(byteBuffer, this.AgentData_Field.EnergyLevel);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.GodLevel);
        this.packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun);
        this.packUUID(byteBuffer, this.AgentData_Field.PreyAgent);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.AgentAccess);
        this.packVariable(byteBuffer, this.AgentData_Field.AgentTextures, 2);
        this.packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID);
        byteBuffer.put((byte)this.GroupData_Fields.size());
        for (final GroupData groupData : this.GroupData_Fields) {
            this.packUUID(byteBuffer, groupData.GroupID);
            this.packLong(byteBuffer, groupData.GroupPowers);
            this.packBoolean(byteBuffer, groupData.AcceptNotices);
        }
        byteBuffer.put((byte)this.AnimationData_Fields.size());
        for (final AnimationData animationData : this.AnimationData_Fields) {
            this.packUUID(byteBuffer, animationData.Animation);
            this.packUUID(byteBuffer, animationData.ObjectID);
        }
        byteBuffer.put((byte)this.GranterBlock_Fields.size());
        final Iterator<Object> iterator3 = this.GranterBlock_Fields.iterator();
        while (iterator3.hasNext()) {
            this.packUUID(byteBuffer, iterator3.next().GranterID);
        }
        byteBuffer.put((byte)this.NVPairData_Fields.size());
        final Iterator<Object> iterator4 = this.NVPairData_Fields.iterator();
        while (iterator4.hasNext()) {
            this.packVariable(byteBuffer, iterator4.next().NVPairs, 2);
        }
        byteBuffer.put((byte)this.VisualParam_Fields.size());
        final Iterator<Object> iterator5 = this.VisualParam_Fields.iterator();
        while (iterator5.hasNext()) {
            this.packByte(byteBuffer, (byte)iterator5.next().ParamValue);
        }
        byteBuffer.put((byte)this.AgentAccess_Fields.size());
        for (final AgentAccess agentAccess : this.AgentAccess_Fields) {
            this.packByte(byteBuffer, (byte)agentAccess.AgentLegacyAccess);
            this.packByte(byteBuffer, (byte)agentAccess.AgentMaxAccess);
        }
        byteBuffer.put((byte)this.AgentInfo_Fields.size());
        final Iterator<Object> iterator7 = this.AgentInfo_Fields.iterator();
        while (iterator7.hasNext()) {
            this.packInt(byteBuffer, iterator7.next().Flags);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final int n = 0;
        this.AgentData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = this.unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AgentPos = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.AgentVel = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.Center = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.Size = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.AtAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.LeftAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.UpAxis = this.unpackLLVector3(byteBuffer);
        this.AgentData_Field.ChangedGrid = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.Far = this.unpackFloat(byteBuffer);
        this.AgentData_Field.Aspect = this.unpackFloat(byteBuffer);
        this.AgentData_Field.Throttles = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.LocomotionState = this.unpackInt(byteBuffer);
        this.AgentData_Field.HeadRotation = this.unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.BodyRotation = this.unpackLLQuaternion(byteBuffer);
        this.AgentData_Field.ControlFlags = this.unpackInt(byteBuffer);
        this.AgentData_Field.EnergyLevel = this.unpackFloat(byteBuffer);
        this.AgentData_Field.GodLevel = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentData_Field.AlwaysRun = this.unpackBoolean(byteBuffer);
        this.AgentData_Field.PreyAgent = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AgentAccess = (this.unpackByte(byteBuffer) & 0xFF);
        this.AgentData_Field.AgentTextures = this.unpackVariable(byteBuffer, 2);
        this.AgentData_Field.ActiveGroupID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final GroupData e = new GroupData();
            e.GroupID = this.unpackUUID(byteBuffer);
            e.GroupPowers = this.unpackLong(byteBuffer);
            e.AcceptNotices = this.unpackBoolean(byteBuffer);
            this.GroupData_Fields.add(e);
        }
        final byte value2 = byteBuffer.get();
        for (int j = 0; j < (value2 & 0xFF); ++j) {
            final AnimationData e2 = new AnimationData();
            e2.Animation = this.unpackUUID(byteBuffer);
            e2.ObjectID = this.unpackUUID(byteBuffer);
            this.AnimationData_Fields.add(e2);
        }
        final byte value3 = byteBuffer.get();
        for (int k = 0; k < (value3 & 0xFF); ++k) {
            final GranterBlock e3 = new GranterBlock();
            e3.GranterID = this.unpackUUID(byteBuffer);
            this.GranterBlock_Fields.add(e3);
        }
        final byte value4 = byteBuffer.get();
        for (int l = 0; l < (value4 & 0xFF); ++l) {
            final NVPairData e4 = new NVPairData();
            e4.NVPairs = this.unpackVariable(byteBuffer, 2);
            this.NVPairData_Fields.add(e4);
        }
        final byte value5 = byteBuffer.get();
        for (int n2 = 0; n2 < (value5 & 0xFF); ++n2) {
            final VisualParam e5 = new VisualParam();
            e5.ParamValue = (this.unpackByte(byteBuffer) & 0xFF);
            this.VisualParam_Fields.add(e5);
        }
        final byte value6 = byteBuffer.get();
        for (int n3 = 0; n3 < (value6 & 0xFF); ++n3) {
            final AgentAccess e6 = new AgentAccess();
            e6.AgentLegacyAccess = (this.unpackByte(byteBuffer) & 0xFF);
            e6.AgentMaxAccess = (this.unpackByte(byteBuffer) & 0xFF);
            this.AgentAccess_Fields.add(e6);
        }
        final byte value7 = byteBuffer.get();
        for (int n4 = n; n4 < (value7 & 0xFF); ++n4) {
            final AgentInfo e7 = new AgentInfo();
            e7.Flags = this.unpackInt(byteBuffer);
            this.AgentInfo_Fields.add(e7);
        }
    }
    
    public static class AgentAccess
    {
        public int AgentLegacyAccess;
        public int AgentMaxAccess;
    }
    
    public static class AgentData
    {
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
    
    public static class AgentInfo
    {
        public int Flags;
    }
    
    public static class AnimationData
    {
        public UUID Animation;
        public UUID ObjectID;
    }
    
    public static class GranterBlock
    {
        public UUID GranterID;
    }
    
    public static class GroupData
    {
        public boolean AcceptNotices;
        public UUID GroupID;
        public long GroupPowers;
    }
    
    public static class NVPairData
    {
        public byte[] NVPairs;
    }
    
    public static class VisualParam
    {
        public int ParamValue;
    }
}
