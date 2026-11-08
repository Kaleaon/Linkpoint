package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID

abstract class LLMotion(val id: LLUUID = LLUUID.generate()) {
    enum class LLMotionBlendType { NORMAL, ADDITIVE }

    open var priority: Int = 0
    open var duration: Float = 0f
    open var easeInDuration: Float = 0.3f
    open var easeOutDuration: Float = 0.3f
    open var loop: Boolean = false
    open var blendType: LLMotionBlendType = LLMotionBlendType.NORMAL

    protected var time: Float = 0f
    var active: Boolean = false

    open fun onActivate(startTime: Float): Boolean { active = true; return true }
    open fun onDeactivate() { active = false }

    abstract fun onUpdate(time: Float): Boolean

    fun update(dt: Float): Boolean {
        time += dt
        if (!loop && duration > 0f && time >= duration) {
            onDeactivate()
            return false
        }
        return onUpdate(if (loop && duration > 0f) time % duration else time)
    }

    fun getEaseInWeight(elapsed: Float): Float =
        if (easeInDuration <= 0f) 1f else (elapsed / easeInDuration).coerceIn(0f, 1f)

    fun getEaseOutWeight(remaining: Float): Float =
        if (easeOutDuration <= 0f) 1f else (remaining / easeOutDuration).coerceIn(0f, 1f)

    fun reset() { time = 0f }
}
