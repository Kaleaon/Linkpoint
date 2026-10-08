package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3

object CharacterConstants {
    const val MAX_JOINTS_PER_MESH = 15
    val MAX_ANIMATED_JOINTS = 216u
    val MAX_JOINTS_PER_MESH_OBJECT = 110u
    val HAND_JOINT_NUM = MAX_ANIMATED_JOINTS - 1u
    val FACE_JOINT_NUM = MAX_ANIMATED_JOINTS - 2u
    const val MAX_PRIORITY = 7
    const val MAX_PELVIS_OFFSET = 5f
    const val JOINT_THRESHOLD_POS_OFFSET = 0.0001f
    const val MAX_ANIM_DURATION = 60f
}

enum class Sex { MALE, FEMALE, UNKNOWN }

enum class UpdateType { NORMAL, HIDDEN, FORCE }

abstract class Character {

    protected val motionController: MotionController = MotionController()
    protected val animationData: MutableMap<String, Any> = mutableMapOf()

    protected var preferredPelvisHeight: Float = 0f
    var sex: Sex = Sex.UNKNOWN
    var appearanceSerialNum: UInt = 0u
    var skeletonSerialNum: UInt = 0u
    var hoverOffset: Vector3 = Vector3()

    abstract fun getAnimationPrefix(): String
    abstract fun getRootJoint(): Joint
    abstract fun getCharacterPosition(): Vector3
    abstract fun getCharacterRotation(): Quaternion
    abstract fun getCharacterVelocity(): Vector3
    abstract fun getCharacterAngularVelocity(): Vector3
    abstract fun getGround(inPos: Vector3, outPos: Vector3, outNorm: Vector3)
    abstract fun getCharacterJoint(i: Int): Joint?
    abstract fun getTimeDilation(): Float
    abstract fun getPixelArea(): Float
    abstract fun getPosGlobalFromAgent(position: Vector3): DoubleArray
    abstract fun getPosAgentFromGlobal(position: DoubleArray): Vector3
    abstract fun addDebugText(text: String)
    abstract fun getID(): LLUUID

    open fun getJoint(name: String): Joint? = getRootJoint().findJoint(name)

    open fun getDebugName(): String = getID().toString()

    fun registerMotion(id: LLUUID, factory: (LLUUID) -> Motion): Boolean =
        motionController.registerMotion(id, factory)

    fun removeMotion(id: LLUUID) = motionController.removeMotion(id)

    fun createMotion(id: LLUUID): Motion? = motionController.createMotion(id)

    fun findMotion(id: LLUUID): Motion? = motionController.findMotion(id)

    open fun startMotion(id: LLUUID, startOffset: Float = 0f): Boolean =
        motionController.startMotion(id, startOffset)

    open fun stopMotion(id: LLUUID, stopImmediate: Boolean = false): Boolean =
        motionController.stopMotion(id, stopImmediate)

    fun isMotionActive(id: LLUUID): Boolean {
        val motion = motionController.findMotion(id) ?: return false
        return motionController.isMotionActive(motion)
    }

    open fun requestStopMotion(motion: Motion) {}

    fun updateMotions(updateType: UpdateType) {
        when (updateType) {
            UpdateType.NORMAL -> motionController.updateMotions(0.033f)
            UpdateType.HIDDEN -> motionController.updateMotionsMinimal()
            UpdateType.FORCE  -> motionController.updateMotions(0.033f, forceUpdate = true)
        }
    }

    fun areAnimationsPaused(): Boolean = motionController.isPaused

    fun setAnimTimeFactor(factor: Float) { motionController.timeFactor = factor }

    fun setTimeStep(timeStep: Float) { motionController.timeStep = timeStep }

    open fun flushAllMotions() = motionController.flushAllMotions()

    open fun deactivateAllMotions() = motionController.deactivateAllMotions()

    fun setAnimationData(name: String, data: Any) { animationData[name] = data }

    fun getAnimationData(name: String): Any? = animationData[name]

    fun removeAnimationData(name: String) { animationData.remove(name) }

    open fun getVolumePos(jointIndex: Int, volumeOffset: Vector3): Vector3 = Vector3()

    open fun findCollisionVolume(volumeId: Int): Joint? = null

    open fun getCollisionVolumeID(name: String): Int = -1

    companion object {
        val instances: MutableList<Character> = mutableListOf()
    }
}
