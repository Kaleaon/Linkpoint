package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID

abstract class LLCharacter {
    protected val joints: MutableMap<String, LLJoint> = linkedMapOf()
    protected val activeMotions: MutableMap<LLUUID, LLMotion> = linkedMapOf()
    protected val morphTargets: MutableMap<String, Float> = linkedMapOf()

    open fun getJoint(name: String): LLJoint? = joints[name]

    protected fun addJoint(joint: LLJoint) { joints[joint.name] = joint }

    fun startMotion(motion: LLMotion, startTime: Float = 0f): Boolean {
        if (motion.onActivate(startTime)) {
            activeMotions[motion.id] = motion
            return true
        }
        return false
    }

    fun stopMotion(id: LLUUID): Boolean {
        val m = activeMotions.remove(id) ?: return false
        m.onDeactivate()
        return true
    }

    fun isMotionActive(id: LLUUID): Boolean = activeMotions.containsKey(id)

    open fun update(dt: Float) {
        val toRemove = mutableListOf<LLUUID>()
        for ((id, motion) in activeMotions) {
            if (!motion.update(dt)) toRemove.add(id)
        }
        toRemove.forEach { id ->
            activeMotions[id]?.onDeactivate()
            activeMotions.remove(id)
        }
    }

    fun setMorphWeight(name: String, weight: Float) {
        morphTargets[name] = weight.coerceIn(0f, 1f)
    }

    fun getMorphWeight(name: String): Float = morphTargets[name] ?: 0f

    abstract fun getCharacterPosition(): com.linkpoint.linden.llmath.Vector3
}
