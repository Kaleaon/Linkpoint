package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llcharacter.LLJoint
import com.linkpoint.linden.llmath.Vector3

enum class AvatarSex { MALE, FEMALE }

class LLAvatarAppearance {

    @set:JvmName("setSexProp")
    var sex: AvatarSex = AvatarSex.FEMALE
    var position: Vector3 = Vector3.ZERO

    private val jointMap: MutableMap<String, LLAvatarJoint> = mutableMapOf()
    private val visualParams: MutableMap<Int, LLVisualParam> = mutableMapOf()
    private val wearables: MutableMap<LLWearableType, MutableList<LLWearable>> = mutableMapOf()
    private val bakeLayerList: MutableList<String> = mutableListOf()

    fun addJoint(joint: LLAvatarJoint) {
        jointMap[joint.name] = joint
    }

    fun getJoint(name: String): LLAvatarJoint? = jointMap[name]

    fun registerVisualParam(param: LLVisualParam) {
        visualParams[param.id] = param
    }

    fun getVisualParam(id: Int): LLVisualParam? = visualParams[id]

    fun setParamWeight(id: Int, weight: Float) {
        visualParams[id]?.setWeight(weight)
    }

    fun addWearable(type: LLWearableType, wearable: LLWearable) {
        wearables.getOrPut(type) { mutableListOf() }.add(wearable)
    }

    fun removeWearable(type: LLWearableType, index: Int) {
        wearables[type]?.removeAt(index)
    }

    fun getWearable(type: LLWearableType, index: Int = 0): LLWearable? =
        wearables[type]?.getOrNull(index)

    fun getWearableCount(type: LLWearableType): Int = wearables[type]?.size ?: 0

    fun setSex(s: AvatarSex) {
        sex = s
    }

    fun getCharacterPosition(): Vector3 = position

    fun setCharacterPosition(pos: Vector3) { position = pos }

    fun addBakeLayer(name: String) { bakeLayerList.add(name) }

    fun getBakeLayers(): List<String> = bakeLayerList.toList()

    fun getJointCount(): Int = jointMap.size

    fun getVisualParamCount(): Int = visualParams.size

    fun getAllJoints(): Map<String, LLAvatarJoint> = jointMap.toMap()

    fun getAllVisualParams(): Map<Int, LLVisualParam> = visualParams.toMap()

    fun buildSkeleton(): Boolean {
        val pelvis = LLAvatarJoint("mPelvis")
        val torso  = LLAvatarJoint("mTorso", pelvis)
        val chest  = LLAvatarJoint("mChest", torso)
        val neck   = LLAvatarJoint("mNeck",  chest)
        val head   = LLAvatarJoint("mHead",  neck)
        val lUpperArm = LLAvatarJoint("mShoulderLeft",  chest)
        val rUpperArm = LLAvatarJoint("mShoulderRight", chest)
        val lLeg   = LLAvatarJoint("mHipLeft",  pelvis)
        val rLeg   = LLAvatarJoint("mHipRight", pelvis)

        listOf(pelvis, torso, chest, neck, head, lUpperArm, rUpperArm, lLeg, rLeg).forEach {
            addJoint(it)
        }
        pelvis.addChild(torso); pelvis.addChild(lLeg); pelvis.addChild(rLeg)
        torso.addChild(chest)
        chest.addChild(neck); chest.addChild(lUpperArm); chest.addChild(rUpperArm)
        neck.addChild(head)
        return true
    }

    override fun toString(): String =
        "LLAvatarAppearance(sex=$sex, joints=${jointMap.size}, params=${visualParams.size})"
}
