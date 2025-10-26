package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ChildAgentUpdate : SLMessage() {
    public ArrayList<AgentAccess> AgentAccess_Fields = ArrayList<>()
    public AgentData AgentData_Field
    public ArrayList<AgentInfo> AgentInfo_Fields = ArrayList<>()
    public ArrayList<AnimationData> AnimationData_Fields = ArrayList<>()
    public ArrayList<GranterBlock> GranterBlock_Fields = ArrayList<>()
    public ArrayList<GroupData> GroupData_Fields = ArrayList<>()
    public ArrayList<NVPairData> NVPairData_Fields = ArrayList<>()
    public ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()

    @JvmStatic
    class AgentAccess {
        public Int AgentLegacyAccess
        public Int AgentMaxAccess
    }

    @JvmStatic
    class AgentData {
        public UUID ActiveGroupID
        public Int AgentAccess
        public UUID AgentID
        public LLVector3 AgentPos
        public ByteArray AgentTextures
        public LLVector3 AgentVel
        public Boolean AlwaysRun
        public Float Aspect
        public LLVector3 AtAxis
        public LLQuaternion BodyRotation
        public LLVector3 Center
        public Boolean ChangedGrid
        public Int ControlFlags
        public Float EnergyLevel
        public Float Far
        public Int GodLevel
        public LLQuaternion HeadRotation
        public LLVector3 LeftAxis
        public Int LocomotionState
        public UUID PreyAgent
        public Long RegionHandle
        public UUID SessionID
        public LLVector3 Size
        public ByteArray Throttles
        public LLVector3 UpAxis
        public Int ViewerCircuitCode
    }

    @JvmStatic
    class AgentInfo {
        public Int Flags
    }

    @JvmStatic
    class AnimationData {
        public UUID Animation
        public UUID ObjectID
    }

    @JvmStatic
    class GranterBlock {
        public UUID GranterID
    }

    @JvmStatic
    class GroupData {
        public Boolean AcceptNotices
        public UUID GroupID
        public Long GroupPowers
    }

    @JvmStatic
    class NVPairData {
        public ByteArray NVPairs
    }

    @JvmStatic
    class VisualParam {
        public Int ParamValue
    }

    public ChildAgentUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        val length: Int = this.AgentData_Field.Throttles.length + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + this.AgentData_Field.AgentTextures.length + 16 + 1 + 1 + (this.GroupData_Fields.size() * 25) + 1 + (this.AnimationData_Fields.size() * 32) + 1 + (this.GranterBlock_Fields.size() * 16) + 1
        val it: Iterator<T> = this.NVPairData_Fields.iterator()
        while (true) {
            val i: Int = length
            if (!it.hasNext()) {
                return i + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AgentAccess_Fields.size() * 2) + 1 + (this.AgentInfo_Fields.size() * 4)
            }
            length = ((NVPairData) it.next()).NVPairs.length + 2 + i
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleChildAgentUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(Ascii.EM)
        packLong(byteBuffer, this.AgentData_Field.RegionHandle)
        packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packLLVector3(byteBuffer, this.AgentData_Field.AgentPos)
        packLLVector3(byteBuffer, this.AgentData_Field.AgentVel)
        packLLVector3(byteBuffer, this.AgentData_Field.Center)
        packLLVector3(byteBuffer, this.AgentData_Field.Size)
        packLLVector3(byteBuffer, this.AgentData_Field.AtAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.LeftAxis)
        packLLVector3(byteBuffer, this.AgentData_Field.UpAxis)
        packBoolean(byteBuffer, this.AgentData_Field.ChangedGrid)
        packFloat(byteBuffer, this.AgentData_Field.Far)
        packFloat(byteBuffer, this.AgentData_Field.Aspect)
        packVariable(byteBuffer, this.AgentData_Field.Throttles, 1)
        packInt(byteBuffer, this.AgentData_Field.LocomotionState)
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation)
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation)
        packInt(byteBuffer, this.AgentData_Field.ControlFlags)
        packFloat(byteBuffer, this.AgentData_Field.EnergyLevel)
        packByte(byteBuffer, (Byte) this.AgentData_Field.GodLevel)
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun)
        packUUID(byteBuffer, this.AgentData_Field.PreyAgent)
        packByte(byteBuffer, (Byte) this.AgentData_Field.AgentAccess)
        packVariable(byteBuffer, this.AgentData_Field.AgentTextures, 2)
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID)
        byteBuffer.put((Byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID)
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
        }
        byteBuffer.put((Byte) this.AnimationData_Fields.size())
        for (AnimationData animationData : this.AnimationData_Fields) {
            packUUID(byteBuffer, animationData.Animation)
            packUUID(byteBuffer, animationData.ObjectID)
        }
        byteBuffer.put((Byte) this.GranterBlock_Fields.size())
        for (GranterBlock granterBlock : this.GranterBlock_Fields) {
            packUUID(byteBuffer, granterBlock.GranterID)
        }
        byteBuffer.put((Byte) this.NVPairData_Fields.size())
        for (NVPairData nVPairData : this.NVPairData_Fields) {
            packVariable(byteBuffer, nVPairData.NVPairs, 2)
        }
        byteBuffer.put((Byte) this.VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (Byte) visualParam.ParamValue)
        }
        byteBuffer.put((Byte) this.AgentAccess_Fields.size())
        for (AgentAccess agentAccess : this.AgentAccess_Fields) {
            packByte(byteBuffer, (Byte) agentAccess.AgentLegacyAccess)
            packByte(byteBuffer, (Byte) agentAccess.AgentMaxAccess)
        }
        byteBuffer.put((Byte) this.AgentInfo_Fields.size())
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packInt(byteBuffer, agentInfo.Flags)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.RegionHandle = unpackLong(byteBuffer)
        this.AgentData_Field.ViewerCircuitCode = unpackInt(byteBuffer)
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.AgentPos = unpackLLVector3(byteBuffer)
        this.AgentData_Field.AgentVel = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Center = unpackLLVector3(byteBuffer)
        this.AgentData_Field.Size = unpackLLVector3(byteBuffer)
        this.AgentData_Field.AtAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.LeftAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.UpAxis = unpackLLVector3(byteBuffer)
        this.AgentData_Field.ChangedGrid = unpackBoolean(byteBuffer)
        this.AgentData_Field.Far = unpackFloat(byteBuffer)
        this.AgentData_Field.Aspect = unpackFloat(byteBuffer)
        this.AgentData_Field.Throttles = unpackVariable(byteBuffer, 1)
        this.AgentData_Field.LocomotionState = unpackInt(byteBuffer)
        this.AgentData_Field.HeadRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.BodyRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.ControlFlags = unpackInt(byteBuffer)
        this.AgentData_Field.EnergyLevel = unpackFloat(byteBuffer)
        this.AgentData_Field.GodLevel = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer)
        this.AgentData_Field.PreyAgent = unpackUUID(byteBuffer)
        this.AgentData_Field.AgentAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.AgentData_Field.AgentTextures = unpackVariable(byteBuffer, 2)
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val groupData: GroupData = GroupData()
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupPowers = unpackLong(byteBuffer)
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
        val b2: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i2 = 0; i2 < b2; i2++) {
            val animationData: AnimationData = AnimationData()
            animationData.Animation = unpackUUID(byteBuffer)
            animationData.ObjectID = unpackUUID(byteBuffer)
            this.AnimationData_Fields.add(animationData)
        }
        val b3: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i3 = 0; i3 < b3; i3++) {
            val granterBlock: GranterBlock = GranterBlock()
            granterBlock.GranterID = unpackUUID(byteBuffer)
            this.GranterBlock_Fields.add(granterBlock)
        }
        val b4: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i4 = 0; i4 < b4; i4++) {
            val nVPairData: NVPairData = NVPairData()
            nVPairData.NVPairs = unpackVariable(byteBuffer, 2)
            this.NVPairData_Fields.add(nVPairData)
        }
        val b5: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i5 = 0; i5 < b5; i5++) {
            val visualParam: VisualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
        val b6: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i6 = 0; i6 < b6; i6++) {
            val agentAccess: AgentAccess = AgentAccess()
            agentAccess.AgentLegacyAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            agentAccess.AgentMaxAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.AgentAccess_Fields.add(agentAccess)
        }
        val b7: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i7 = 0; i7 < b7; i7++) {
            val agentInfo: AgentInfo = AgentInfo()
            agentInfo.Flags = unpackInt(byteBuffer)
            this.AgentInfo_Fields.add(agentInfo)
        }
    }
}
