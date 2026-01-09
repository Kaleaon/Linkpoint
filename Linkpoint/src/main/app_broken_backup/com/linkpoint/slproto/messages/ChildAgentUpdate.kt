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

class ChildAgentUpdate : SLMessage {
    ArrayList<AgentAccess> AgentAccess_Fields = ArrayList<>()
    AgentData AgentData_Field
    ArrayList<AgentInfo> AgentInfo_Fields = ArrayList<>()
    ArrayList<AnimationData> AnimationData_Fields = ArrayList<>()
    ArrayList<GranterBlock> GranterBlock_Fields = ArrayList<>()
    ArrayList<GroupData> GroupData_Fields = ArrayList<>()
    ArrayList<NVPairData> NVPairData_Fields = ArrayList<>()
    ArrayList<VisualParam> VisualParam_Fields = ArrayList<>()

    class AgentAccess {
        Int AgentLegacyAccess
        Int AgentMaxAccess
    }

    class AgentData {
        UUID ActiveGroupID
        Int AgentAccess
        UUID AgentID
        LLVector3 AgentPos
        ByteArray AgentTextures
        LLVector3 AgentVel
        Boolean AlwaysRun
        float Aspect
        LLVector3 AtAxis
        LLQuaternion BodyRotation
        LLVector3 Center
        Boolean ChangedGrid
        Int ControlFlags
        float EnergyLevel
        float Far
        Int GodLevel
        LLQuaternion HeadRotation
        LLVector3 LeftAxis
        Int LocomotionState
        UUID PreyAgent
        Long RegionHandle
        UUID SessionID
        LLVector3 Size
        ByteArray Throttles
        LLVector3 UpAxis
        Int ViewerCircuitCode
    }

    class AgentInfo {
        Int Flags
    }

    class AnimationData {
        UUID Animation
        UUID ObjectID
    }

    class GranterBlock {
        UUID GranterID
    }

    class GroupData {
        Boolean AcceptNotices
        UUID GroupID
        Long GroupPowers
    }

    class NVPairData {
        ByteArray NVPairs
    }

    class VisualParam {
        Int ParamValue
    }

    ChildAgentUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        var length: Int = this.AgentData_Field.Throttles.size + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + this.AgentData_Field.AgentTextures.size + 16 + 1 + 1 + (this.GroupData_Fields.size() * 25) + 1 + (this.AnimationData_Fields.size() * 32) + 1 + (this.GranterBlock_Fields.size() * 16) + 1
        Iterator<T> it = this.NVPairData_Fields.iterator()
        while (true) {
            var i: Int = length
            if (!it.hasNext()) {
                return i + 1 + (this.VisualParam_Fields.size() * 1) + 1 + (this.AgentAccess_Fields.size() * 2) + 1 + (this.AgentInfo_Fields.size() * 4)
            }
            length = ((it as NVPairData).next()).NVPairs.size + 2 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleChildAgentUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
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
        packByte(byteBuffer, (this as byte).AgentData_Field.GodLevel)
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun)
        packUUID(byteBuffer, this.AgentData_Field.PreyAgent)
        packByte(byteBuffer, (this as byte).AgentData_Field.AgentAccess)
        packVariable(byteBuffer, this.AgentData_Field.AgentTextures, 2)
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID)
        byteBuffer.put((this as byte).GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID)
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
        }
        byteBuffer.put((this as byte).AnimationData_Fields.size())
        for (AnimationData animationData : this.AnimationData_Fields) {
            packUUID(byteBuffer, animationData.Animation)
            packUUID(byteBuffer, animationData.ObjectID)
        }
        byteBuffer.put((this as byte).GranterBlock_Fields.size())
        for (GranterBlock granterBlock : this.GranterBlock_Fields) {
            packUUID(byteBuffer, granterBlock.GranterID)
        }
        byteBuffer.put((this as byte).NVPairData_Fields.size())
        for (NVPairData nVPairData : this.NVPairData_Fields) {
            packVariable(byteBuffer, nVPairData.NVPairs, 2)
        }
        byteBuffer.put((this as byte).VisualParam_Fields.size())
        for (VisualParam visualParam : this.VisualParam_Fields) {
            packByte(byteBuffer, (visualParam as byte).ParamValue)
        }
        byteBuffer.put((this as byte).AgentAccess_Fields.size())
        for (AgentAccess agentAccess : this.AgentAccess_Fields) {
            packByte(byteBuffer, (agentAccess as byte).AgentLegacyAccess)
            packByte(byteBuffer, (agentAccess as byte).AgentMaxAccess)
        }
        byteBuffer.put((this as byte).AgentInfo_Fields.size())
        for (AgentInfo agentInfo : this.AgentInfo_Fields) {
            packInt(byteBuffer, agentInfo.Flags)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            GroupData groupData = GroupData()
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupPowers = unpackLong(byteBuffer)
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i2 in 0 until b2) {
            AnimationData animationData = AnimationData()
            animationData.Animation = unpackUUID(byteBuffer)
            animationData.ObjectID = unpackUUID(byteBuffer)
            this.AnimationData_Fields.add(animationData)
        }
        byte b3 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i3 in 0 until b3) {
            GranterBlock granterBlock = GranterBlock()
            granterBlock.GranterID = unpackUUID(byteBuffer)
            this.GranterBlock_Fields.add(granterBlock)
        }
        byte b4 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i4 in 0 until b4) {
            NVPairData nVPairData = NVPairData()
            nVPairData.NVPairs = unpackVariable(byteBuffer, 2)
            this.NVPairData_Fields.add(nVPairData)
        }
        byte b5 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i5 in 0 until b5) {
            VisualParam visualParam = VisualParam()
            visualParam.ParamValue = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.VisualParam_Fields.add(visualParam)
        }
        byte b6 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i6 in 0 until b6) {
            AgentAccess agentAccess = AgentAccess()
            agentAccess.AgentLegacyAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            agentAccess.AgentMaxAccess = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.AgentAccess_Fields.add(agentAccess)
        }
        byte b7 = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i7 in 0 until b7) {
            AgentInfo agentInfo = AgentInfo()
            agentInfo.Flags = unpackInt(byteBuffer)
            this.AgentInfo_Fields.add(agentInfo)
        }
    }
}
