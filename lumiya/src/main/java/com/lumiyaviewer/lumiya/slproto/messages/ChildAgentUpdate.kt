package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.types.UUID
import com.lumiyaviewer.lumiya.slproto.types.UUIDPool
import java.nio.ByteBuffer
import java.util.ArrayList

class ChildAgentUpdate : SLMessage {
    var AgentAccess_Fields: ArrayList<AgentAccess> = ArrayList()
    var AgentData_Field: AgentData = AgentData()
    var AgentInfo_Fields: ArrayList<AgentInfo> = ArrayList()
    var AnimationData_Fields: ArrayList<AnimationData> = ArrayList()
    var GranterBlock_Fields: ArrayList<GranterBlock> = ArrayList()
    var GroupData_Fields: ArrayList<GroupData> = ArrayList()
    var NVPairData_Fields: ArrayList<NVPairData> = ArrayList()
    var VisualParam_Fields: ArrayList<VisualParam> = ArrayList()

    class AgentAccess {
        var AgentLegacyAccess: Int = 0
        var AgentMaxAccess: Int = 0
    }

    class AgentData {
        var ActiveGroupID: UUID = UUIDPool.ZeroUUID
        var AgentAccess: Int = 0
        var AgentID: UUID = UUIDPool.ZeroUUID
        var AgentPos: LLVector3 = LLVector3()
        var AgentTextures: ByteArray = ByteArray(0)
        var AgentVel: LLVector3 = LLVector3()
        var AlwaysRun: Boolean = false
        var Aspect: Float = 0f
        var AtAxis: LLVector3 = LLVector3()
        var BodyRotation: LLQuaternion = LLQuaternion()
        var Center: LLVector3 = LLVector3()
        var ChangedGrid: Boolean = false
        var ControlFlags: Int = 0
        var EnergyLevel: Float = 0f
        var Far: Float = 0f
        var GodLevel: Int = 0
        var HeadRotation: LLQuaternion = LLQuaternion()
        var LeftAxis: LLVector3 = LLVector3()
        var LocomotionState: Int = 0
        var PreyAgent: UUID = UUIDPool.ZeroUUID
        var RegionHandle: Long = 0
        var SessionID: UUID = UUIDPool.ZeroUUID
        var Size: LLVector3 = LLVector3()
        var Throttles: ByteArray = ByteArray(0)
        var UpAxis: LLVector3 = LLVector3()
        var ViewerCircuitCode: Int = 0
    }

    class AgentInfo {
        var Flags: Int = 0
    }

    class AnimationData {
        var Animation: UUID = UUIDPool.ZeroUUID
        var ObjectID: UUID = UUIDPool.ZeroUUID
    }

    class GranterBlock {
        var GranterID: UUID = UUIDPool.ZeroUUID
    }

    class GroupData {
        var AcceptNotices: Boolean = false
        var GroupID: UUID = UUIDPool.ZeroUUID
        var GroupPowers: Long = 0
    }

    class NVPairData {
        var NVPairs: ByteArray = ByteArray(0)
    }

    class VisualParam {
        var ParamValue: Int = 0
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var length = this.AgentData_Field.Throttles.size + 138 + 4 + 12 + 12 + 4 + 4 + 1 + 1 + 16 + 1 + 2 + this.AgentData_Field.AgentTextures.size + 16 + 1 + 1 + (this.GroupData_Fields.size * 25) + 1 + (this.AnimationData_Fields.size * 32) + 1 + (this.GranterBlock_Fields.size * 16) + 1
        for (nv in NVPairData_Fields) {
            length += nv.NVPairs.size + 2
        }
        return length + 1 + (this.VisualParam_Fields.size * 1) + 1 + (this.AgentAccess_Fields.size * 2) + 1 + (this.AgentInfo_Fields.size * 4)
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleChildAgentUpdate(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.put(25.toByte())
        byteBuffer.putLong(this.AgentData_Field.RegionHandle)
        byteBuffer.putInt(this.AgentData_Field.ViewerCircuitCode)
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
        byteBuffer.putInt(this.AgentData_Field.LocomotionState)
        packLLQuaternion(byteBuffer, this.AgentData_Field.HeadRotation)
        packLLQuaternion(byteBuffer, this.AgentData_Field.BodyRotation)
        byteBuffer.putInt(this.AgentData_Field.ControlFlags)
        packFloat(byteBuffer, this.AgentData_Field.EnergyLevel)
        byteBuffer.put(this.AgentData_Field.GodLevel.toByte())
        packBoolean(byteBuffer, this.AgentData_Field.AlwaysRun)
        packUUID(byteBuffer, this.AgentData_Field.PreyAgent)
        byteBuffer.put(this.AgentData_Field.AgentAccess.toByte())
        packVariable(byteBuffer, this.AgentData_Field.AgentTextures, 2)
        packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID)
        byteBuffer.put(this.GroupData_Fields.size.toByte())
        for (groupData in this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID)
            byteBuffer.putLong(groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
        }
        byteBuffer.put(this.AnimationData_Fields.size.toByte())
        for (animationData in this.AnimationData_Fields) {
            packUUID(byteBuffer, animationData.Animation)
            packUUID(byteBuffer, animationData.ObjectID)
        }
        byteBuffer.put(this.GranterBlock_Fields.size.toByte())
        for (granterBlock in this.GranterBlock_Fields) {
            packUUID(byteBuffer, granterBlock.GranterID)
        }
        byteBuffer.put(this.NVPairData_Fields.size.toByte())
        for (nVPairData in this.NVPairData_Fields) {
            packVariable(byteBuffer, nVPairData.NVPairs, 2)
        }
        byteBuffer.put(this.VisualParam_Fields.size.toByte())
        for (visualParam in this.VisualParam_Fields) {
            byteBuffer.put(visualParam.ParamValue.toByte())
        }
        byteBuffer.put(this.AgentAccess_Fields.size.toByte())
        for (agentAccess in this.AgentAccess_Fields) {
            byteBuffer.put(agentAccess.AgentLegacyAccess.toByte())
            byteBuffer.put(agentAccess.AgentMaxAccess.toByte())
        }
        byteBuffer.put(this.AgentInfo_Fields.size.toByte())
        for (agentInfo in this.AgentInfo_Fields) {
            byteBuffer.putInt(agentInfo.Flags)
        }
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.RegionHandle = byteBuffer.long
        this.AgentData_Field.ViewerCircuitCode = byteBuffer.int
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
        this.AgentData_Field.LocomotionState = byteBuffer.int
        this.AgentData_Field.HeadRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.BodyRotation = unpackLLQuaternion(byteBuffer)
        this.AgentData_Field.ControlFlags = byteBuffer.int
        this.AgentData_Field.EnergyLevel = unpackFloat(byteBuffer)
        this.AgentData_Field.GodLevel = byteBuffer.get().toInt() and 0xFF
        this.AgentData_Field.AlwaysRun = unpackBoolean(byteBuffer)
        this.AgentData_Field.PreyAgent = unpackUUID(byteBuffer)
        this.AgentData_Field.AgentAccess = byteBuffer.get().toInt() and 0xFF
        this.AgentData_Field.AgentTextures = unpackVariable(byteBuffer, 2)
        this.AgentData_Field.ActiveGroupID = unpackUUID(byteBuffer)
        
        val b = byteBuffer.get().toInt() and 0xFF
        for (i in 0 until b) {
            val groupData = GroupData()
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupPowers = byteBuffer.long
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
        
        val b2 = byteBuffer.get().toInt() and 0xFF
        for (i2 in 0 until b2) {
            val animationData = AnimationData()
            animationData.Animation = unpackUUID(byteBuffer)
            animationData.ObjectID = unpackUUID(byteBuffer)
            this.AnimationData_Fields.add(animationData)
        }
        
        val b3 = byteBuffer.get().toInt() and 0xFF
        for (i3 in 0 until b3) {
            val granterBlock = GranterBlock()
            granterBlock.GranterID = unpackUUID(byteBuffer)
            this.GranterBlock_Fields.add(granterBlock)
        }
        
        val b4 = byteBuffer.get().toInt() and 0xFF
        for (i4 in 0 until b4) {
            val nVPairData = NVPairData()
            nVPairData.NVPairs = unpackVariable(byteBuffer, 2)
            this.NVPairData_Fields.add(nVPairData)
        }
        
        val b5 = byteBuffer.get().toInt() and 0xFF
        for (i5 in 0 until b5) {
            val visualParam = VisualParam()
            visualParam.ParamValue = byteBuffer.get().toInt() and 0xFF
            this.VisualParam_Fields.add(visualParam)
        }
        
        val b6 = byteBuffer.get().toInt() and 0xFF
        for (i6 in 0 until b6) {
            val agentAccess = AgentAccess()
            agentAccess.AgentLegacyAccess = byteBuffer.get().toInt() and 0xFF
            agentAccess.AgentMaxAccess = byteBuffer.get().toInt() and 0xFF
            this.AgentAccess_Fields.add(agentAccess)
        }
        
        val b7 = byteBuffer.get().toInt() and 0xFF
        for (i7 in 0 until b7) {
            val agentInfo = AgentInfo()
            agentInfo.Flags = byteBuffer.int
            this.AgentInfo_Fields.add(agentInfo)
        }
    }
}
