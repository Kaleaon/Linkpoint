package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import com.linkpoint.linden.llmath.slerp

class LLPose {
    private val jointStates: MutableMap<String, JointState> = linkedMapOf()

    fun getOrCreate(jointName: String): JointState =
        jointStates.getOrPut(jointName) { JointState(Joint(name = jointName)) }

    fun getJointState(jointName: String): JointState? = jointStates[jointName]

    fun clearAllJoints() = jointStates.clear()

    fun apply(joint: LLJoint, weight: Float = 1f) {
        val state = jointStates[joint.name] ?: return
        if (weight >= 1f) {
            joint.setLocalPosition(state.position)
            joint.setLocalRotation(state.rotation)
        } else {
            val blendedPos = Vector3(
                joint.localPosition.x + (state.position.x - joint.localPosition.x) * weight,
                joint.localPosition.y + (state.position.y - joint.localPosition.y) * weight,
                joint.localPosition.z + (state.position.z - joint.localPosition.z) * weight
            )
            joint.setLocalPosition(blendedPos)
            joint.setLocalRotation(slerp(weight, joint.localRotation, state.rotation))
        }
    }

    fun blend(other: LLPose, weight: Float): LLPose {
        val result = LLPose()
        for ((name, state) in jointStates) {
            val target = other.jointStates[name]
            val out = result.getOrCreate(name)
            if (target == null) {
                out.position = state.position
                out.rotation = state.rotation
            } else {
                out.position = Vector3(
                    state.position.x + (target.position.x - state.position.x) * weight,
                    state.position.y + (target.position.y - state.position.y) * weight,
                    state.position.z + (target.position.z - state.position.z) * weight
                )
                out.rotation = slerp(weight, state.rotation, target.rotation)
            }
        }
        return result
    }

    fun jointNames(): Set<String> = jointStates.keys.toSet()
}
