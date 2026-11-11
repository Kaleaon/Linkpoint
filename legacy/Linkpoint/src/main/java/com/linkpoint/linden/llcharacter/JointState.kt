package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3

enum class JointPriority(val value: Int) {
    USE_MOTION_PRIORITY(-1),
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    HIGHER(3),
    HIGHEST(4),
    ADDITIVE(7);

    companion object {
        fun fromInt(v: Int): JointPriority = entries.firstOrNull { it.value == v } ?: LOW
    }
}

class Joint(
    var name: String = "",
    var parent: Joint? = null
) {
    val children: MutableList<Joint> = mutableListOf()
    var position: Vector3 = Vector3()
    var rotation: Quaternion = Quaternion()
    var scale: Vector3 = Vector3(1f, 1f, 1f)
    var skinOffset: Vector3 = Vector3()
    var jointNum: Int = -1
    var defaultPosition: Vector3 = Vector3()
    var defaultScale: Vector3 = Vector3(1f, 1f, 1f)

    val attachmentPosOverrides: MutableMap<LLUUID, Vector3> = mutableMapOf()
    val attachmentScaleOverrides: MutableMap<LLUUID, Vector3> = mutableMapOf()

    fun getRoot(): Joint = parent?.getRoot() ?: this

    fun findJoint(jointName: String): Joint? {
        if (name == jointName) return this
        for (child in children) {
            child.findJoint(jointName)?.let { return it }
        }
        return null
    }

    fun addChild(joint: Joint) {
        joint.parent = this
        children.add(joint)
    }

    fun removeChild(joint: Joint) {
        joint.parent = null
        children.remove(joint)
    }

    fun removeAllChildren() {
        children.forEach { it.parent = null }
        children.clear()
    }

    fun getWorldPosition(): Vector3 {
        val p = parent ?: return position
        val parentWorld = p.getWorldPosition()
        val rotated = p.getWorldRotation() * position
        return parentWorld + rotated
    }

    fun getWorldRotation(): Quaternion {
        val p = parent ?: return rotation
        return p.getWorldRotation() * rotation
    }

    fun updateWorldTransform() {
        children.forEach { it.updateWorldTransform() }
    }

    fun addAttachmentPosOverride(pos: Vector3, meshId: LLUUID) {
        attachmentPosOverrides[meshId] = pos
    }

    fun removeAttachmentPosOverride(meshId: LLUUID) {
        attachmentPosOverrides.remove(meshId)
    }

    fun hasAttachmentPosOverride(): Boolean = attachmentPosOverrides.isNotEmpty()

    fun clearAttachmentPosOverrides() { attachmentPosOverrides.clear() }

    fun addAttachmentScaleOverride(scale: Vector3, meshId: LLUUID) {
        attachmentScaleOverrides[meshId] = scale
    }

    fun removeAttachmentScaleOverride(meshId: LLUUID) {
        attachmentScaleOverrides.remove(meshId)
    }

    fun clearAttachmentScaleOverrides() { attachmentScaleOverrides.clear() }

    open fun isAnimatable(): Boolean = true
}

class JointState(var joint: Joint? = null) {

    enum class BlendPhase { INACTIVE, EASE_IN, ACTIVE, EASE_OUT }

    enum class Usage(val mask: UInt) {
        POS(1u), ROT(2u), SCALE(4u)
    }

    var usage: UInt = 0u
    var weight: Float = 0f
    var position: Vector3 = Vector3()
    var rotation: Quaternion = Quaternion()
    var scale: Vector3 = Vector3(1f, 1f, 1f)
    var priority: JointPriority = JointPriority.USE_MOTION_PRIORITY

    fun setJoint(j: Joint?): Boolean {
        joint = j
        return j != null
    }

    fun getJointName(): String = joint?.name ?: ""
}
