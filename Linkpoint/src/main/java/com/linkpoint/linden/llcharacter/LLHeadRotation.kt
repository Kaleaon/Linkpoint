package com.linkpoint.linden.llcharacter

import com.linkpoint.linden.llcommon.LLUUID
import com.linkpoint.linden.llmath.Quaternion
import com.linkpoint.linden.llmath.Vector3
import kotlin.math.atan2
import kotlin.math.asin

class LLHeadRotation(
    var target: Vector3 = Vector3(0f, 1f, 0f),
    var speed: Float = 3f
) : LLMotion(LLUUID.generate()) {

    override var priority: Int = 2
    override var duration: Float = 0f
    override var loop: Boolean = true

    private val headJointName = "mHead"
    private var currentYaw: Float = 0f
    private var currentPitch: Float = 0f

    override fun onUpdate(time: Float): Boolean {
        val dir = Vector3(target.x, target.y, target.z)
        dir.normalize()
        val targetYaw = atan2(dir.x.toDouble(), dir.y.toDouble()).toFloat()
        val targetPitch = asin((-dir.z).toDouble().coerceIn(-1.0, 1.0)).toFloat()
        currentYaw = lerp(currentYaw, targetYaw, 0.1f)
        currentPitch = lerp(currentPitch, targetPitch, 0.1f)
        return true
    }

    fun getHeadRotation(): Quaternion {
        val q = Quaternion()
        q.setEulerAngles(currentPitch, 0f, currentYaw)
        return q
    }

    private fun lerp(a: Float, b: Float, t: Float): Float = a + (b - a) * t
}
